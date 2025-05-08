package com.loong.android.media.local

import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import com.loong.android.media.common.ContextSupplier
import com.loong.android.media.common.TLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 媒体存储管理器
 */
object MediaStoreManager {

    private val audioProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA
    )

    fun init() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_EJECT)
            addDataScheme("file")
        }
        ContextSupplier.get().registerReceiver(MediaReceiver(), intentFilter)

        val file = Environment.getExternalStorageDirectory()
        TLog.i("init: $file")
        if (file != null) {
            CoroutineScope(Dispatchers.IO).launch {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                }
            }
        }
    }

    /**
     * 挂载卷
     */
    internal fun onMounted(data: String?) {
        TLog.i("onMounted: $data")
        ContextSupplier.get()
    }

    /**
     * 查询所有音频
     */
    suspend fun queryAllAudios() = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        ContextSupplier.get()
            .contentResolver
            .query(uri, audioProjection, null, null, null)
            ?.use { it.cursorToMediaMetadata() }
    }

    /**
     * 查询指定卷中的音频
     */
    suspend fun queryVolumeAudios(volumeName: String) = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.VOLUME_NAME} = ?"
        val args = arrayOf(volumeName.lowercase())
        ContextSupplier.get()
            .contentResolver
            .query(uri, audioProjection, selection, args, null)
            ?.use { it.cursorToMediaMetadata() }
    }

    /**
     * 查询该文件夹中的音频
     */
    suspend fun queryPathAudios(path: String) = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val normalizedPath = if (path.endsWith("/")) path else "$path/"

        val selection = """
        ${MediaStore.Audio.Media.DATA} LIKE ? 
        AND instr(substr(${MediaStore.Audio.Media.DATA}, LENGTH(?) + 1), '/') = 0
    """.trimIndent()

        val args = arrayOf("$normalizedPath%", normalizedPath)
        ContextSupplier.get()
            .contentResolver
            .query(uri, audioProjection, selection, args, null)
            ?.use { it.cursorToMediaMetadata() }
    }

    private fun Cursor.cursorToMediaMetadata(): List<MediaMetadata>? {
        try {
            val titleColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColum = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColum = getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            if (count == 0) return emptyList()

            val list = mutableListOf<MediaMetadata>()
            while (moveToNext()) {
                val path = getString(pathColumn)
                val file = File(path)
                if (!file.exists()) {
                    continue
                }
                val title = getString(titleColumn)
                val artist = getString(artistColumn)
                val album = getString(albumColum)
                val duration = getInt(durationColum)
                list += MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setDurationMs(duration.toLong())
                    .setArtworkUri(file.toUri())
                    .build()
            }
            return list
        } catch (e: Exception) {
            TLog.e(e, "cursorToMediaMetadata: fail")
            return null
        }
    }

}