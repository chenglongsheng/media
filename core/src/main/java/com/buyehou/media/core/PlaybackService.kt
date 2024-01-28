package com.buyehou.media.core

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

private const val TAG = "PlaybackService"

/**
 * 播放服务
 *
 * @author buyehou
 */
class PlaybackService : MediaLibraryService() {

    private var session: MediaLibrarySession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        Log.d(TAG, "onGetSession: $controllerInfo")
        return session
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.putExtra("action", "open_media_app")
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val forwardingPlayer = ForwardingPlayer.get(this)
        session = MediaLibrarySession.Builder(this, forwardingPlayer, MediaSessionCallback())
            .setId("PlaybackService")
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        session?.release()
        session = null
        super.onDestroy()
    }

}