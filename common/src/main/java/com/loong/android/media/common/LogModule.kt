package com.loong.android.media.common

import android.content.Context

/**
 * 日志工具组件初始化
 */
class LogModule : MediaModule() {
    override fun create(context: Context) {
        Timber.plant(ConsoleLoggerTree("LM"))
        log.i("create: ")
    }
}