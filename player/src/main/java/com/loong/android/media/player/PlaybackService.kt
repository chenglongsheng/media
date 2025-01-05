package com.loong.android.media.player

import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import timber.log.Timber

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
        Timber.tag(TAG).i("onCreate: ")
        session = MediaLibrarySession.Builder(this, HolderPlayer(), MediaLibrarySessionCallback())
            .build()
    }

    override fun onDestroy() {
        Timber.tag(TAG).i("onDestroy: ")
        session = session?.run {
            player.release()
            release()
            null
        }
        super.onDestroy()
    }
}