package com.loong.android.media.data

import android.net.Uri

data class MediaStoreAudio(
    val id: Long,
    val title: String,
    val artist: String,
    val path: String,
    val uri: Uri
)