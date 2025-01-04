package com.loong.android.media.player

import android.util.Log
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

class PlaybackService : MediaLibraryService() {

    companion object {
        private const val TAG = "PlaybackService"
    }

    private var session: MediaLibrarySession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
        session = MediaLibrarySession.Builder(this, HolderPlayer(), MediaLibrarySessionCallback())
            .build()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: ")
        session = session?.run {
            player.release()
            release()
            null
        }
        super.onDestroy()
    }
}