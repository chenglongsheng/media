package com.buyehou.media.core

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import java.util.concurrent.ExecutionException

/**
 * 播放管理器
 *
 * @author buyehou
 */
class PlaybackManager private constructor(context: Context) {

    private var mediaBrowser: MediaBrowser? = null

    init {
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaBrowser.Builder(context, token)
            .buildAsync()
        future.addListener({
            try {
                Log.d(TAG, "The session accepted the connection.")
                mediaBrowser = future.get()
            } catch (e: ExecutionException) {
                Log.e(TAG, "The session rejected the connection.", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    companion object {
        private const val TAG = "PlaybackManager"

        @Volatile
        private var instance: PlaybackManager? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = PlaybackManager(context.applicationContext)
                    }
                }
            }
        }

        fun get(): PlaybackManager {
            return instance!!
        }
    }

}