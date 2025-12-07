package com.loong.android.media.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio")
data class AudioEntity(
    @PrimaryKey val path: String,
    val title: String = path.substringBeforeLast('.'),
    val artist: String? = null,
    val album: String? = null,
    val durationMs: Long = 0,
    val isMetadataExtracted: Boolean = false, // 标记是否已读取过 ID3
    val addedTimestamp: Long = System.currentTimeMillis()
)