package com.buyehou.media

import android.app.Application
import android.util.Log
import com.buyehou.media.core.PlaybackManager

private const val TAG = "MediaApp"

/**
 * @author buyehou
 */
class MediaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        PlaybackManager.init(this)
    }

}