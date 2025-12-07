package com.loong.android.media.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.content.ContextCompat
import com.loong.android.media.local.db.AudioDao
import com.loong.android.media.local.model.AudioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class UsbModule(private val audioDao: AudioDao) {

    companion object {
        private const val TAG = "UsbModule"
    }

    private lateinit var app: Context
    private val scanner = AudioScanner()

    // 全局协程作用域
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 1. 多设备任务管理：Key 是 USB 根路径，Value 是对应的扫描 Job
    private val scanJobs = ConcurrentHashMap<String, Job>()
    private val activeUsbRoots = ConcurrentHashMap.newKeySet<String>()

    // 2. 元数据提取任务：全局单例 Job，防止多个 USB 同时插入炸 CPU
    private var extractionJob: Job? = null

    private val extractionMutex = Mutex()

    // 3. 限制 Metadata 解析并发数为 2，避免车机卡顿 (Modern Android Practice)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val metadataDispatcher = Dispatchers.Default.limitedParallelism(2)

    val allSongs: Flow<List<AudioEntity>> = audioDao.getAllAudio()

    fun initialize(context: Context) {
        app = context.applicationContext
        // 注册广播
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_EJECT) // 拔出
            addAction(Intent.ACTION_MEDIA_UNMOUNTED) // 卸载
            addDataScheme("file")
        }
        ContextCompat.registerReceiver(
            app,
            UsbMountReceiver(),
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    /**
     * 处理 USB 挂载：扫描文件 -> 入库 -> 触发解析
     */
    private fun startScan(usbRootPath: String) {
        activeUsbRoots.add(usbRootPath)

        // 取消该路径旧的扫描任务（如果存在）
        scanJobs[usbRootPath]?.cancel()

        val newJob = scope.launch {
            Log.d(TAG, "Starting scan for: $usbRootPath")

            // 1. 清理该 USB 的旧数据 (只删这个 USB 的，不影响其他)
            audioDao.deleteByRootPath(usbRootPath)

            // 2. NIO 快速扫描
            val rawFiles = scanner.scanFiles(usbRootPath)
            Log.d(TAG, "Found ${rawFiles.size} files in $usbRootPath")

            // 3. 批量入库
            rawFiles.chunked(500).forEach { batch ->
                if (!isActive) return@forEach

                if (activeUsbRoots.contains(usbRootPath)) {
                    audioDao.insertBatch(batch)
                } else {
                    Log.w(TAG, "root path: $usbRootPath, is removed")
                }
            }

            // 4. 触发全局元数据解析
            triggerMetadataExtraction()
        }

        // 记录任务
        scanJobs[usbRootPath] = newJob
    }

    /**
     * 处理 USB 卸载：取消任务 -> 清库
     */
    private fun stopScan(usbRootPath: String) {
        activeUsbRoots.remove(usbRootPath)

        // 1. 取消该 USB 的扫描任务
        scanJobs.remove(usbRootPath)?.cancel()

        // 2. 立即从数据库移除该 USB 的歌曲
        scope.launch {
            audioDao.deleteByRootPath(usbRootPath)
            Log.d(TAG, "Removed data for: $usbRootPath")
        }
    }

    /**
     * 保证同一时间只有一个 Extraction 循环在跑，但它会不断处理 DB 里的新数据
     */
    private fun triggerMetadataExtraction() {
        if (extractionMutex.tryLock()) {
            try {
                // 如果当前已经在跑，就不需要再次启动，
                // 但为了保险（比如之前的任务跑完了），我们重新检查
                if (extractionJob?.isActive == true) return

                extractionJob = scope.launch {
                    execMetadataExtractionLoop()
                }
            } finally {
                extractionMutex.unlock()
            }
        }
    }

    private suspend fun CoroutineScope.execMetadataExtractionLoop() {
        val retriever = MediaMetadataRetriever()
        var processedCount = 0

        try {
            // 循环直到没有未处理的数据
            while (isActive) {
                // 获取一批未处理的数据 (不区分 USB，统一处理)
                val unprocessed = audioDao.getUnprocessedDocs()

                if (unprocessed.isEmpty()) {
                    break // 全部搞定，退出循环
                }

                for (entity in unprocessed) {
                    if (!isActive) break

                    try {
                        retriever.setDataSource(entity.path)
                        val title =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                ?: entity.path
                        val artist =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        val duration =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                ?.toLongOrNull() ?: 0L

                        audioDao.updateMetadata(entity.path, title, artist, duration)
                    } catch (_: Exception) {
                        // 可能是文件损坏或中途拔出
                        Log.w(TAG, "Meta fail: ${entity.path}")
                        audioDao.updateMetadata(
                            entity.path,
                            entity.path.substringBeforeLast('.'),
                            null,
                            0
                        )
                    }

                    processedCount++

                    // 性能优化策略：每处理 20 个，休息 5ms
                    if (processedCount % 20 == 0) {
                        delay(5)
                    } else {
                        ensureActive()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Extraction loop error", e)
        } finally {
            // 必须释放资源
            try {
                retriever.release()
            } catch (_: IOException) {
            }
        }
    }

    inner class UsbMountReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            // 关键：使用 intent.data.path 获取纯净路径 (e.g., /storage/usb0)
            // intent.dataString 会带 file:// 前缀
            val path = intent?.data?.path

            Log.i(TAG, "USB Event: $action, Path: $path")

            if (path.isNullOrEmpty()) return

            when (action) {
                Intent.ACTION_MEDIA_MOUNTED -> {
                    startScan(path)
                }

                Intent.ACTION_MEDIA_EJECT, Intent.ACTION_MEDIA_UNMOUNTED -> {
                    stopScan(path)
                }
            }
        }
    }
}