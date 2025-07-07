package com.loong.android.media.local

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import com.loong.android.media.common.ContextSupplier
import com.loong.android.media.common.TLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 媒体存储管理器
 */
object MediaStoreManager {

    private val context by lazy { ContextSupplier.get().applicationContext }

    /**
     * 查询音频字段
     */
    private val audioProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DATA
    )

    /**
     * 检查访问存储权限
     */
    fun assertReadPermission() {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (!granted) throw Exception("have not read storage permission")
    }

    suspend inline fun <R> ContentResolver.queryCancellable(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
        crossinline action: Cursor.() -> R?
    ): R? = suspendCancellableCoroutine { cont ->
        try {
            assertReadPermission()
            val cancellationSignal = CancellationSignal()
            cont.invokeOnCancellation { cancellationSignal.cancel() }
            val cursor =
                query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal)
            val r = cursor?.action()
            cursor?.close()
            cont.resume(r)
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    /**
     * 查询所有音频
     */
    suspend fun queryAllAudios() = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.queryCancellable(uri, audioProjection) {
            cursorToMediaMetadata()
        }
    }

    /**
     * 查询指定卷中的音频
     */
    suspend fun queryVolumeAudios(volumeName: String) = withContext(Dispatchers.IO) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.VOLUME_NAME} = ?"
        val args = arrayOf(volumeName.lowercase())
        context.contentResolver.queryCancellable(uri, audioProjection, selection, args) {
            cursorToMediaMetadata()
        }
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
        context.contentResolver
            .queryCancellable(uri, audioProjection, selection, args) {
                cursorToMediaMetadata()
            }
    }

    private fun Cursor.cursorToMediaMetadata(): List<MediaMetadata>? {
        try {
            val titleColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColum = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
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
                list += MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setDescription(path)
                    .build()
            }
            return list
        } catch (e: Exception) {
            TLog.e(e, "cursorToMediaMetadata: fail")
            return null
        }
    }

}