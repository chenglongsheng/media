package com.loong.android.media.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import com.loong.android.media.common.TLog

/**
 * 默认实现播放器监听器
 */
@UnstableApi
open class PlayerListener : Player.Listener {
    override fun onEvents(
        player: Player,
        events: Player.Events
    ) {
        val eventList = events.run { (0 until size()).map { get(it) } }
        TLog.d("onEvents() called with: player = $player, events = $eventList")
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        TLog.d("onTimelineChanged() called with: timeline = $timeline, reason = $reason")
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int
    ) {
        TLog.d("onMediaItemTransition() called with: mediaItem = $mediaItem, reason = $reason")
    }

    override fun onTracksChanged(tracks: Tracks) {
        TLog.d("onTracksChanged() called with: tracks = $tracks")
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        TLog.d("onMediaMetadataChanged() called with: mediaMetadata = $mediaMetadata")
    }

    override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
        TLog.d("onPlaylistMetadataChanged() called with: mediaMetadata = $mediaMetadata")
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        TLog.d("onIsLoadingChanged() called with: isLoading = $isLoading")
    }

    override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
        TLog.d("onAvailableCommandsChanged() called with: availableCommands = $availableCommands")
    }

    override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
        TLog.d("onTrackSelectionParametersChanged() called with: parameters = $parameters")
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        TLog.d("onPlaybackStateChanged() called with: playbackState = $playbackState")
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        TLog.d("onPlayWhenReadyChanged() called with: playWhenReady = $playWhenReady, reason = $reason")
    }

    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        TLog.d("onPlaybackSuppressionReasonChanged() called with: playbackSuppressionReason = $playbackSuppressionReason")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        TLog.d("onIsPlayingChanged() called with: isPlaying = $isPlaying")
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        TLog.d("onRepeatModeChanged() called with: repeatMode = $repeatMode")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        TLog.d("onShuffleModeEnabledChanged() called with: shuffleModeEnabled = $shuffleModeEnabled")
    }

    override fun onPlayerError(error: PlaybackException) {
        TLog.d("onPlayerError() called with: error = $error")
    }

    override fun onPlayerErrorChanged(error: PlaybackException?) {
        TLog.d("onPlayerErrorChanged() called with: error = $error")
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        TLog.d("onPositionDiscontinuity() called with: oldPosition = $oldPosition, newPosition = $newPosition, reason = $reason")
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        TLog.d("onPlaybackParametersChanged() called with: playbackParameters = $playbackParameters")
    }

    override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
        TLog.d("onSeekBackIncrementChanged() called with: seekBackIncrementMs = $seekBackIncrementMs")
    }

    override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
        TLog.d("onSeekForwardIncrementChanged() called with: seekForwardIncrementMs = $seekForwardIncrementMs")
    }

    override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
        TLog.d("onMaxSeekToPreviousPositionChanged() called with: maxSeekToPreviousPositionMs = $maxSeekToPreviousPositionMs")
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        TLog.d("onAudioSessionIdChanged() called with: audioSessionId = $audioSessionId")
    }

    override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
        TLog.d("onAudioAttributesChanged() called with: audioAttributes = $audioAttributes")
    }

    override fun onVolumeChanged(volume: Float) {
        TLog.d("onVolumeChanged() called with: volume = $volume")
    }

    override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
        TLog.d("onSkipSilenceEnabledChanged() called with: skipSilenceEnabled = $skipSilenceEnabled")
    }

    override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
        TLog.d("onDeviceInfoChanged() called with: deviceInfo = $deviceInfo")
    }

    override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
        TLog.d("onDeviceVolumeChanged() called with: volume = $volume, muted = $muted")
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        TLog.d("onVideoSizeChanged() called with: videoSize = $videoSize")
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        TLog.d("onSurfaceSizeChanged() called with: width = $width, height = $height")
    }

    override fun onRenderedFirstFrame() {
        TLog.d("onRenderedFirstFrame() called")
    }

    override fun onCues(cueGroup: CueGroup) {
        TLog.d("onCues() called with: cueGroup = $cueGroup")
    }

    override fun onMetadata(metadata: Metadata) {
        TLog.d("onMetadata() called with: metadata = $metadata")
    }
}