package com.loong.android.media.common

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber

class LoggerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.plant(Timber.DebugTree())
        Timber.d("create: ")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}