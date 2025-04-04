package com.loong.android.media.common

import android.content.Context

/**
 * 通用组件初始化
 */
class CommonModule : MediaModule() {
    override fun create(context: Context) {
        log.i("create: ")
        ContextSupplier.init(context)
    }
}