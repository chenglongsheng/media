package com.loong.android.media.common

import android.content.Context

/**
 * 媒体模块
 */
abstract class MediaModule {
    protected val log = TLog.tag(javaClass.simpleName)

    /**
     * 初始化
     */
    abstract fun create(context: Context)
}