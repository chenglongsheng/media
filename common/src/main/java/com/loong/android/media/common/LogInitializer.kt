package com.loong.android.media.common

import android.content.Context
import androidx.startup.Initializer

/**
 * 日志工具组件初始化
 */
class LogInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.plant(ConsoleLoggerTree("LM"))
        TLog.i("create: ")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}