package com.loong.android.media.player

import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

class PlaybackService : MediaLibraryService() {

    private var session: MediaLibrarySession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }

    override fun onCreate() {
        super.onCreate()
        session = MediaLibrarySession.Builder(this, HolderPlayer(), MediaLibrarySessionCallback())
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        session?.release()
        session = null
    }
}