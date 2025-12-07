package com.loong.android.media.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.loong.android.media.common.TLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 播放服务控制器
 */
object PlayController {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering
    private val _mediaMetadata = MutableStateFlow(MediaMetadata.EMPTY)
    val mediaMetadata: StateFlow<MediaMetadata> = _mediaMetadata
    private val _playlist = MutableStateFlow { emptyList<MediaItem>() }
    val playlist: StateFlow<() -> List<MediaItem>> = _playlist

    private var browser: MediaBrowser? = null

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _isBuffering.value = playbackState == Player.STATE_BUFFERING
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            _mediaMetadata.value = mediaMetadata
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            _playlist.value = {
                browser.let { browser ->
                    if (browser == null) {
                        emptyList()
                    } else {
                        (0 until timeline.windowCount).map {
                            browser.getMediaItemAt(it)
                        }
                    }
                }
            }
        }
    }

    fun init(context: Context) {
        val componentName = ComponentName(context, PlaybackService::class.java)
        val sessionToken = SessionToken(context, componentName)
        val future = MediaBrowser.Builder(context, sessionToken)
            .setApplicationLooper(context.mainLooper)
            .buildAsync()

        future.addListener({
            try {
                val browser = future.get()
                this.browser = browser
                browser.addListener(listener)

                _isPlaying.value = browser.isPlaying
                _isBuffering.value = browser.playbackState == Player.STATE_BUFFERING
                _mediaMetadata.value = browser.mediaMetadata
                _playlist.value = {
                    (0 until browser.mediaItemCount).map {
                        browser.getMediaItemAt(it)
                    }
                }

            } catch (e: Exception) {
                TLog.e(e, "init: fail=$e")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * 播放
     */
    fun play() {
        browser?.play()
    }

    /**
     * 暂停
     */
    fun pause() {
        browser?.pause()
    }

    /**
     * 停止
     */
    fun stop() {
        browser?.stop()
    }

    /**
     * 上一曲
     */
    fun prev() {
        browser?.seekToPrevious()
    }

    /**
     * 下一曲
     */
    fun next() {
        browser?.seekToNext()
    }

    /**
     * 后退一段
     */
    fun back() {
        browser?.seekBack()
    }

    /**
     * 前进一段
     */
    fun forward() {
        browser?.seekForward()
    }

    /**
     * 快进到指定位置
     */
    fun seekTo(index: Int, position: Long = 0L) {
        browser?.seekTo(index, position)
    }

    /**
     * 播放歌单
     */
    fun play(playlistData: PlaylistData) {
        val id = PlaylistData.save(playlistData)
        val requestMetadata = RequestMetadata.Builder()
            .setSearchQuery(id)
            .build()
        val mediaItem = MediaItem.Builder()
            .setRequestMetadata(requestMetadata)
            .build()
        browser?.setMediaItem(mediaItem)
    }

}