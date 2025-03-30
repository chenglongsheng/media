package com.loong.android.media.common

import android.content.Context
import androidx.startup.Initializer

/**
 * 通用组件初始化
 */
class CommonInitializer : Initializer<Unit> {
    private val log = TLog.tag("CommonInitializer")

    override fun create(context: Context) {
        log.i("create: ")
        ContextSupplier.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(LogInitializer::class.java)
    }
}