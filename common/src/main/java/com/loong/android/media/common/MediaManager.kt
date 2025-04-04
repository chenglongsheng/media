package com.loong.android.media.common

import android.content.Context

/**
 * 媒体管理器
 */
interface MediaManager {

    /**
     * 初始化
     */
    fun init(context: Context)

    /**
     * 是否初始化
     */
    fun isInit(): Boolean

    /**
     * 获取已经初始化的模块
     */
    fun <C> getModule(clazz: Class<C>): MediaModule?

    /**
     * 添加模块
     */
    fun addModule(module: MediaModule)

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MediaManagerImpl() }
    }
}