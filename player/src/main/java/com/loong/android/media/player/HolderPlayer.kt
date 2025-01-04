package com.loong.android.media.player

import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.BasePlayer
import androidx.media3.common.C
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
class HolderPlayer : BasePlayer() {
    override fun getApplicationLooper(): Looper {
        return Looper.getMainLooper()
    }

    override fun addListener(listener: Player.Listener) {
        // no code
    }

    override fun removeListener(listener: Player.Listener) {
        // no code
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {
        // no code
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        // no code
    }

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {
        // no code
    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {
        // no code
    }

    override fun replaceMediaItems(
        fromIndex: Int,
        toIndex: Int,
        mediaItems: MutableList<MediaItem>
    ) {
        // no code
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {
        // no code
    }

    override fun getAvailableCommands(): Player.Commands {
        return Player.Commands.EMPTY
    }

    override fun prepare() {
        // no code
    }

    override fun getPlaybackState(): Int {
        return Player.STATE_IDLE
    }

    override fun getPlaybackSuppressionReason(): Int {
        return Player.PLAYBACK_SUPPRESSION_REASON_NONE
    }

    override fun getPlayerError(): PlaybackException? {
        // no code
        return null
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        // no code
    }

    override fun getPlayWhenReady(): Boolean {
        return false
    }

    override fun setRepeatMode(repeatMode: Int) {
        // no code
    }

    override fun getRepeatMode(): Int {
        return Player.REPEAT_MODE_OFF
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        // no code
    }

    override fun getShuffleModeEnabled(): Boolean {
        return false
    }

    override fun isLoading(): Boolean {
        return false
    }

    override fun seekTo(
        mediaItemIndex: Int,
        positionMs: Long,
        seekCommand: Int,
        isRepeatingCurrentItem: Boolean
    ) {
        // no code
    }

    override fun getSeekBackIncrement(): Long {
        return C.DEFAULT_SEEK_BACK_INCREMENT_MS
    }

    override fun getSeekForwardIncrement(): Long {
        // no code
        return C.DEFAULT_SEEK_FORWARD_INCREMENT_MS
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        return C.DEFAULT_MAX_SEEK_TO_PREVIOUS_POSITION_MS
    }

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {
        // no code
    }

    override fun getPlaybackParameters(): PlaybackParameters {
        return PlaybackParameters.DEFAULT
    }

    override fun stop() {
        // no code
    }

    override fun release() {
        // no code
    }

    override fun getCurrentTracks(): Tracks {
        return Tracks.EMPTY
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT
    }

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {
        // no code
    }

    override fun getMediaMetadata(): MediaMetadata {
        return MediaMetadata.EMPTY
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        return MediaMetadata.EMPTY
    }

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {
        // no code
    }

    override fun getCurrentTimeline(): Timeline {
        return Timeline.EMPTY
    }

    override fun getCurrentPeriodIndex(): Int {
        return C.INDEX_UNSET
    }

    override fun getCurrentMediaItemIndex(): Int {
        return C.INDEX_UNSET
    }

    override fun getDuration(): Long {
        return C.TIME_UNSET
    }

    override fun getCurrentPosition(): Long {
        return C.TIME_UNSET
    }

    override fun getBufferedPosition(): Long {
        return C.TIME_UNSET
    }

    override fun getTotalBufferedDuration(): Long {
        return C.TIME_UNSET
    }

    override fun isPlayingAd(): Boolean {
        return false
    }

    override fun getCurrentAdGroupIndex(): Int {
        return C.INDEX_UNSET
    }

    override fun getCurrentAdIndexInAdGroup(): Int {
        return C.INDEX_UNSET
    }

    override fun getContentPosition(): Long {
        return C.TIME_UNSET
    }

    override fun getContentBufferedPosition(): Long {
        return C.TIME_UNSET
    }

    override fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.DEFAULT
    }

    override fun setVolume(volume: Float) {
        // no code
    }

    override fun getVolume(): Float {
        return 0f
    }

    override fun clearVideoSurface() {
        // no code
    }

    override fun clearVideoSurface(surface: Surface?) {
        // no code
    }

    override fun setVideoSurface(surface: Surface?) {
        // no code
    }

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        // no code
    }

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        // no code
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        // no code
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
        // no code
    }

    override fun setVideoTextureView(textureView: TextureView?) {
        // no code
    }

    override fun clearVideoTextureView(textureView: TextureView?) {
        // no code
    }

    override fun getVideoSize(): VideoSize {
        return VideoSize.UNKNOWN
    }

    override fun getSurfaceSize(): Size {
        return Size.UNKNOWN
    }

    override fun getCurrentCues(): CueGroup {
        return CueGroup.EMPTY_TIME_ZERO
    }

    override fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo.UNKNOWN
    }

    override fun getDeviceVolume(): Int {
        return 0
    }

    override fun isDeviceMuted(): Boolean {
        // no code
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun setDeviceVolume(volume: Int) {
        // no code
    }

    override fun setDeviceVolume(volume: Int, flags: Int) {
        // no code
    }

    @Deprecated("Deprecated in Java")
    override fun increaseDeviceVolume() {
        // no code
    }

    override fun increaseDeviceVolume(flags: Int) {
        // no code
    }

    @Deprecated("Deprecated in Java")
    override fun decreaseDeviceVolume() {
        // no code
    }

    override fun decreaseDeviceVolume(flags: Int) {
        // no code
    }

    @Deprecated("Deprecated in Java")
    override fun setDeviceMuted(muted: Boolean) {
        // no code
    }

    override fun setDeviceMuted(muted: Boolean, flags: Int) {
        // no code
    }

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {
        // no code
    }
}