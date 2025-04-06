package com.loong.android.media.data

import android.content.ContentUris
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.loong.android.media.common.ContextSupplier
import com.loong.android.media.common.TLog
import com.loong.android.media.data.scan.AudioScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 媒体存储管理器
 */
object MediaStoreManager {

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
                    val scanner = AudioScanner()
                    scanner.scanDirectory(file)
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
     * 查询音频
     */
    suspend fun queryAudios() = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )

        val selection = null
        ContextSupplier.get().contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )?.use {
            val audios = mutableListOf<MediaStoreAudio>()

            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            val count = it.count
            TLog.d("queryAudios: count=$count")
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val path = it.getString(pathColumn)

                val contentUri = ContentUris.withAppendedId(uri, id)

                audios += MediaStoreAudio(id, title, artist, path, contentUri)
            }
            audios
        } ?: emptyList()
    }

    fun listen() {
        ContextSupplier.get().contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri?, flags: Int) {
                    TLog.d("onChange: ")
                }
            }
        )
    }

}