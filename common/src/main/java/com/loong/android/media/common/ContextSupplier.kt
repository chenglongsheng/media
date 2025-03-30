package com.loong.android.media.common

import android.app.Application
import android.content.Context

/**
 * 提供全局Application
 */
object ContextSupplier {
    /**
     * application实例
     */
    private lateinit var application: Application

    /**
     * 初始化
     */
    internal fun init(context: Context) {
        application = context as Application
    }

    /**
     * 获取application实例
     */
    fun get() = application
}