package com.loong.android.media.common

import android.content.Context
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 媒体管理器实现
 */
class MediaManagerImpl : MediaManager {

    private var isInit = false

    private val modules = CopyOnWriteArraySet<MediaModule>()

    override fun init(context: Context) {
        if (isInit) return
        modules.add(LogModule())
        modules.add(CommonModule())
        modules.forEach { it.create(context.applicationContext) }
    }

    override fun isInit(): Boolean {
        return isInit
    }

    override fun <C> getModule(clazz: Class<C>): MediaModule? {
        return modules.find { clazz.name == it.javaClass.name }
    }

    override fun addModule(module: MediaModule) {
        modules.add(module)
    }
}