package com.loong.android.media.player

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

/**
 * 本地播放器
 */
@SuppressLint("UnsafeOptInUsageError")
class LocalPlayer(player: Player) : ForwardingPlayer(player) {

    companion object {
        fun get(context: Context): LocalPlayer {
            val player = ExoPlayer.Builder(context)
                .setMaxSeekToPreviousPositionMs(Long.MAX_VALUE)
                .setName("LocalPlayer")
                .build()
            return LocalPlayer(player)
        }
    }
}