package com.loong.android.media.common

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber

class CommonInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.d("create: ")
        ContextSupplier.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(LoggerInitializer::class.java)
    }
}