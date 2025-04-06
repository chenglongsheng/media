package com.loong.android.media.data.scan

import android.net.Uri
import java.io.File

/**
 * 媒体扫描器
 */
interface MediaScanner {
    /**
     * 扫描目录
     */
    fun scanDirectory(dir: File)

    /**
     * 扫描文件
     */
    fun scanFile(file: File): Uri?
}