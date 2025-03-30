package com.loong.android.media.player

import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.loong.android.media.common.TLog

class PlaybackService : MediaLibraryService() {

    private var session: MediaLibrarySession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }

    override fun onCreate() {
        super.onCreate()
        TLog.i("onCreate: ")
        val player = LocalPlayer.get(this.application)
        session = MediaLibrarySession.Builder(this, player, MediaLibrarySessionCallback())
            .build()
    }

    override fun onDestroy() {
        TLog.i("onDestroy: ")
        session = session?.run {
            player.release()
            release()
            null
        }
        super.onDestroy()
    }
}