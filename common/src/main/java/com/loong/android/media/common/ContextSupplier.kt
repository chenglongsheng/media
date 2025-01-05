package com.loong.android.media.common

import android.app.Application
import android.content.Context

object ContextSupplier {
    private lateinit var application: Application

    internal fun init(context: Context) {
        application = context as Application
    }

    fun get() = application
}