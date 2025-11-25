package com.loong.android.media.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usb_audio")
data class UsbAudioEntity(
    @PrimaryKey val path: String,
    val fileName: String,
    val title: String? = null,
    val artist: String? = null,
    val durationMs: Long = 0,
    val isMetadataExtracted: Boolean = false, // 标记是否已读取过 ID3
    val addedTimestamp: Long = System.currentTimeMillis()
)