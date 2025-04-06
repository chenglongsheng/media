package com.loong.android.media.data.scan

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.OperationCanceledException
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import com.loong.android.media.common.TLog
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

@RequiresApi(Build.VERSION_CODES.O)
class AudioScanner : MediaScanner {

    private val activeScan = mutableListOf<Scan>()

    private fun addActiveScan(scan: Scan) {
        synchronized(activeScan) { activeScan.add(scan) }
    }

    private fun removeActiveScan(scan: Scan) {
        synchronized(activeScan) { activeScan.remove(scan) }
    }

    override fun scanDirectory(dir: File) {
        val file: File
        try {
            file = dir.canonicalFile
        } catch (e: IOException) {
            TLog.e(e, "Couldn't canonicalize directory to scan $dir")
            return
        }
        try {
            val scan = Scan(file)
            scan.run()
        } catch (e: FileNotFoundException) {
            TLog.e(e, "Couldn't find directory to scan")
        } catch (ignored: OperationCanceledException) {

        }
    }

    override fun scanFile(file: File): Uri? {
        val canonicalFile: File
        try {
            canonicalFile = file.canonicalFile
        } catch (e: IOException) {
            TLog.e(e, "Couldn't canonicalize file to scan $file")
            return null
        }
        try {
            Scan(file).use { scan ->
                scan.run()
                return null
            }
        } catch (e: FileNotFoundException) {
            TLog.e(e, "Couldn't find file to scan")
            return null
        } catch (ignored: OperationCanceledException) {
            // No-op.
            return null
        }
    }

    inner class Scan(val root: File) : Runnable, FileVisitor<Path>, AutoCloseable {
        private val signal = CancellationSignal()

        override fun run() {
            addActiveScan(this)
            try {
                signal.throwIfCanceled()
                Files.walkFileTree(root.toPath(), this)
            } finally {
                removeActiveScan(this)
            }
        }

        override fun preVisitDirectory(
            dir: Path,
            attrs: BasicFileAttributes?
        ): FileVisitResult? {
            signal.throwIfCanceled()
            TLog.v("preVisitDirectory: dir = $dir")
            val name = dir.fileName?.toString() ?: return FileVisitResult.CONTINUE
            return if (name.startsWith(".")) FileVisitResult.SKIP_SUBTREE else FileVisitResult.CONTINUE
        }

        override fun visitFile(
            file: Path,
            attrs: BasicFileAttributes?
        ): FileVisitResult? {
            signal.throwIfCanceled()
            TLog.v("visitFile: file = $file")
            val fileObj = file.toFile()

            if (file.toFile().isAudio()) {
                processAudioFile(fileObj)
            }

            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(
            file: Path,
            exc: IOException?
        ): FileVisitResult? {
            TLog.e(exc, "visitFileFailed: file = $file")
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(
            dir: Path,
            exc: IOException?
        ): FileVisitResult? {
            TLog.v("postVisitDirectory: dir = $dir, exc = $exc")
            return FileVisitResult.CONTINUE
        }

        override fun close() {
        }

        /**
         * 处理音频元数据
         */
        private fun processAudioFile(file: File) {
            val mmr = MediaMetadataRetriever()
            var inputStream: InputStream?
            try {
                inputStream = file.inputStream()
                mmr.setDataSource(inputStream.fd)
                val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val artist =
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                TLog.i("processAudioFile: $title-$artist")
            } catch (e: Exception) {
                TLog.e(e, "processAudioFile extractMetadata fail: $file")
            } finally {
                mmr.release()
                inputStream = null
            }
        }

        private fun File.isAudio(): Boolean = extension.let {
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
            mimeType?.startsWith("audio/", true) == true
        }
    }

}