package com.loong.android.media.local

import android.util.Log
import android.webkit.MimeTypeMap
import com.loong.android.media.local.model.AudioEntity
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.extension

/**
 * 音频扫描器
 */
class AudioScanner() {

    companion object {
        private const val TAG = "AudioScanner"
    }

    /**
     * 极速扫描：只返回文件对象，不读取 ID3
     * @param rootPath USB 挂载根目录 (例如 /storage/1234-5678)
     */
    fun scanFiles(rootPath: String): List<AudioEntity> {
        val startPath = Paths.get(rootPath)
        if (!Files.exists(startPath)) {
            return emptyList()
        }

        val audioFiles = mutableListOf<AudioEntity>()

        try {
            Files.walkFileTree(startPath, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val extension = file.extension.lowercase()
                    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                    if (mimeType?.startsWith("audio") == true) {
                        val absPath = file.toAbsolutePath().toString()
                        audioFiles.add(AudioEntity(absPath))
                    }
                    return FileVisitResult.CONTINUE
                }

                override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
                    // 处理权限拒绝等异常，跳过该文件
                    Log.w(TAG, "visitFileFailed: $file, e=$exc")
                    return FileVisitResult.CONTINUE
                }
            })
        } catch (e: IOException) {
            Log.e(TAG, "Scan failed", e)
        }
        return audioFiles
    }
}