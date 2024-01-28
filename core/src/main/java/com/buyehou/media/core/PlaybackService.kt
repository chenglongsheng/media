package com.buyehou.media.core

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
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
        val exoPlayer = ExoPlayer.Builder(this)
            .build()
        session = MediaLibrarySession.Builder(this, exoPlayer, MediaSessionCallback())
            .setId("PlaybackService")
            .setSessionActivity(pendingIntent)
            .build()

        // Online radio:
        val uri = Uri.parse("http://listen.livestreamingservice.com/181-xsoundtrax_128k.mp3")
        // 1 kHz test sound:
        // val uri = Uri.parse("https://www.mediacollege.com/audio/tone/files/1kHz_44100Hz_16bit_05sec.mp3")
        // 10 kHz test sound:
        // val uri = Uri.parse("https://www.mediacollege.com/audio/tone/files/10kHz_44100Hz_16bit_05sec.mp3")
        // Sweep from 20 to 20 kHz
        // val uri = Uri.parse("https://www.churchsoundcheck.com/CSC_sweep_20-20k.wav")

        val mediaMetadata = MediaMetadata.Builder()
            .setAlbumTitle("你好")
            .setArtist("David Bowie")
            .setTitle("Heroes")
            .build()
        val mediaItem = MediaItem.Builder()
            .setMediaId("")
            .setUri(uri)
            .setMediaMetadata(mediaMetadata)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        session?.release()
        session = null
        super.onDestroy()
    }

}