package com.loong.android.media.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.loong.android.media.common.TLog

class PlayManager private constructor(context: Context) {

    private var browser: MediaBrowser? = null

    init {
        initMediaBrowser(context)
    }

    private fun initMediaBrowser(context: Context) {
        TLog.i("initMediaBrowser: start")
        val componentName = ComponentName(context, PlaybackService::class.java)
        val sessionToken = SessionToken(context, componentName)
        val future = MediaBrowser.Builder(context, sessionToken)
            .buildAsync()
        future.addListener({
            try {
                TLog.i("initMediaBrowser: success")
                val mediaBrowser = future.get()
                browser = mediaBrowser
            } catch (e: Exception) {
                TLog.e(e, "initMediaBrowser: fail=$e")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: PlayManager? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = PlayManager(context)
                    }
                }
            }
        }

        fun get() = instance!!
    }
}