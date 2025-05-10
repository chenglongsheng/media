package com.loong.android.media.player

import android.os.Bundle
import java.util.UUID

/**
 * 播放歌单数据
 */
class PlaylistData private constructor(
    /**
     * 音源id
     */
    var sid: Int = -1,

    /**
     * 播放类型
     */
    var playType: Int = -1,

    /**
     * 歌单对象
     */
    var playlist: Any? = null,

    /**
     * 音频列表
     */
    var audioList: List<Any>? = null,

    /**
     * 播放的列表下表
     */
    var index: Int = 0,

    /**
     * 立即播放
     */
    var isPlay: Boolean = true,

    /**
     * 歌单合集
     */
    var playlistGroup: String? = null,

    /**
     * 其他数据
     */
    var extra: Bundle? = null
) {
    /**
     * 重置字段
     */
    fun reset() {
        sid = -1
        playType = -1
        playlist = null
        audioList = null
        index = 0
        isPlay = true
        playlistGroup = null
        extra = null
    }

    override fun toString(): String {
        return "PlaylistData(sid=$sid, playType=$playType, playlist=$playlist, audioList=$audioList, index=$index, isPlay=$isPlay, playlistGroup=$playlistGroup, extra=$extra)"
    }

    companion object {
        private const val MAX_POOL_SIZE = 10
        private val pool = ArrayDeque<PlaylistData>(MAX_POOL_SIZE)

        private val cache = mutableMapOf<String, PlaylistData>()

        fun obtain(): PlaylistData {
            return synchronized(pool) {
                if (pool.isEmpty()) PlaylistData() else pool.removeFirst()
            }
        }

        internal fun recycle(data: PlaylistData) {
            synchronized(pool) {
                pool.addLast(data)
            }
        }

        internal fun save(data: PlaylistData): String {
            synchronized(cache) {
                val id = UUID.randomUUID().toString()
                cache[id] = data
                return id
            }
        }

        internal fun read(key: String): PlaylistData? {
            synchronized(cache) {
                return cache.remove(key)
            }
        }
    }
}