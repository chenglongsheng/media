package com.buyehou.media.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.BasePlayer
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

/**
 * @author buyehou
 */
class ForwardingPlayer(private val context: Context) : BasePlayer() {

    override fun getApplicationLooper(): Looper {
        return context.mainLooper
    }

    override fun addListener(listener: Player.Listener) {

    }

    override fun removeListener(listener: Player.Listener) {

    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {

    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {

    }

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {

    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {

    }

    override fun replaceMediaItems(
        fromIndex: Int,
        toIndex: Int,
        mediaItems: MutableList<MediaItem>
    ) {

    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {

    }

    override fun getAvailableCommands(): Player.Commands {
        return Player.Commands.Builder()
            .add(COMMAND_SET_MEDIA_ITEM)
            .build()
    }

    override fun prepare() {

    }

    override fun getPlaybackState(): Int {
        return STATE_IDLE
    }

    override fun getPlaybackSuppressionReason(): Int {
        return PLAYBACK_SUPPRESSION_REASON_NONE
    }

    override fun getPlayerError(): PlaybackException? {
        return null
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {

    }

    override fun getPlayWhenReady(): Boolean {
        return false
    }

    override fun setRepeatMode(repeatMode: Int) {

    }

    override fun getRepeatMode(): Int {
        return REPEAT_MODE_OFF
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {

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

    }

    override fun getSeekBackIncrement(): Long {
        return 0
    }

    override fun getSeekForwardIncrement(): Long {
        return 0
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        return 0
    }

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {

    }

    override fun getPlaybackParameters(): PlaybackParameters {
        return PlaybackParameters.DEFAULT
    }

    override fun stop() {

    }

    override fun release() {

    }

    override fun getCurrentTracks(): Tracks {
        return Tracks.EMPTY
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT
    }

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {

    }

    override fun getMediaMetadata(): MediaMetadata {
        return MediaMetadata.EMPTY
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        return MediaMetadata.EMPTY
    }

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {

    }

    override fun getCurrentTimeline(): Timeline {
        return Timeline.EMPTY
    }

    override fun getCurrentPeriodIndex(): Int {
        return 0
    }

    override fun getCurrentMediaItemIndex(): Int {
        return 0
    }

    override fun getDuration(): Long {
        return 0
    }

    override fun getCurrentPosition(): Long {
        return 0
    }

    override fun getBufferedPosition(): Long {
        return 0
    }

    override fun getTotalBufferedDuration(): Long {
        return 0
    }

    override fun isPlayingAd(): Boolean {
        return false
    }

    override fun getCurrentAdGroupIndex(): Int {
        return 0
    }

    override fun getCurrentAdIndexInAdGroup(): Int {
        return 0
    }

    override fun getContentPosition(): Long {
        return 0
    }

    override fun getContentBufferedPosition(): Long {
        return 0
    }

    override fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.DEFAULT
    }

    override fun setVolume(volume: Float) {

    }

    override fun getVolume(): Float {
        return 1f
    }

    override fun clearVideoSurface() {

    }

    override fun clearVideoSurface(surface: Surface?) {

    }

    override fun setVideoSurface(surface: Surface?) {

    }

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {

    }

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {

    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {

    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {

    }

    override fun setVideoTextureView(textureView: TextureView?) {

    }

    override fun clearVideoTextureView(textureView: TextureView?) {

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
        return 1
    }

    override fun isDeviceMuted(): Boolean {
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun setDeviceVolume(volume: Int) {

    }

    override fun setDeviceVolume(volume: Int, flags: Int) {

    }

    @Deprecated("Deprecated in Java")
    override fun increaseDeviceVolume() {

    }

    override fun increaseDeviceVolume(flags: Int) {

    }

    @Deprecated("Deprecated in Java")
    override fun decreaseDeviceVolume() {

    }

    override fun decreaseDeviceVolume(flags: Int) {

    }

    @Deprecated("Deprecated in Java")
    override fun setDeviceMuted(muted: Boolean) {

    }

    override fun setDeviceMuted(muted: Boolean, flags: Int) {

    }

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var holderPlayer: ForwardingPlayer? = null

        fun get(context: Context): ForwardingPlayer {
            return holderPlayer ?: synchronized(this) {
                ForwardingPlayer(context).also {
                    holderPlayer = it
                }
            }
        }
    }
}