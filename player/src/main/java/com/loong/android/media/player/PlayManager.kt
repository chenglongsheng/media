package com.loong.android.media.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.loong.android.media.common.TLog

@SuppressLint("UnsafeOptInUsageError")
object PlayManager {
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying
    private val _isBuffering = MutableLiveData(false)
    val isBuffering: LiveData<Boolean> = _isBuffering
    private val _metadata = MutableLiveData(MediaMetadata.EMPTY)
    val metadata: LiveData<MediaMetadata> = _metadata

    private val handlerThread = HandlerThread("PlayManager")
    private lateinit var handler: Handler

    private var browser: MediaBrowser? = null

    private val listener = object : PlayerListener() {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _isPlaying.postValue(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            _isBuffering.postValue(playbackState == Player.STATE_BUFFERING)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            _metadata.postValue(mediaMetadata)
        }
    }

    fun init(context: Context) {
        TLog.i("init: start")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        postOrRun {
            val componentName = ComponentName(context, PlaybackService::class.java)
            val sessionToken = SessionToken(context, componentName)
            val future = MediaBrowser.Builder(context, sessionToken)
                .setApplicationLooper(handlerThread.looper)
                .buildAsync()
            future.addListener({
                try {
                    TLog.i("init: success")
                    val browser = future.get()
                    this.browser = browser

                    browser.addListener(listener)
                } catch (e: Exception) {
                    TLog.e(e, "init: fail=$e")
                }
            }, MoreExecutors.directExecutor())
        }
    }

    private fun postOrRun(r: () -> Unit) {
        if (Looper.myLooper() != handlerThread.looper) {
            handler.post { r.invoke() }
        } else {
            r.invoke()
        }
    }

    /**
     * 播放
     */
    fun play() = postOrRun {
        browser?.play()
    }

    /**
     * 暂停
     */
    fun pause() = postOrRun {
        browser?.pause()
    }

    /**
     * 停止
     */
    fun stop() = postOrRun {
        browser?.stop()
    }

    /**
     * 上一曲
     */
    fun prev() = postOrRun {
        browser?.seekToPrevious()
    }

    /**
     * 下一曲
     */
    fun next() = postOrRun {
        browser?.seekToNext()
    }

    /**
     * 后退一段
     */
    fun back() = postOrRun {
        browser?.seekBack()
    }

    /**
     * 前进一段
     */
    fun forward() = postOrRun {
        browser?.seekForward()
    }

    /**
     * 快进到指定位置
     */
    fun seekTo(position: Long) = postOrRun {
        browser?.seekTo(position)
    }

    /**
     * 播放指定下标
     */
    fun playIndex(index: Int) = postOrRun {
        browser?.seekTo(index, 0L)
    }

    /**
     * 播放歌单
     */
    fun play(playlistData: PlaylistData) = postOrRun {
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