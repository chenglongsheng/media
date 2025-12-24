package com.loong.android.media.ui.model

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Event : Route

    @Serializable
    data object Media : Route

    @Serializable
    data object Recorder : Route

    @Serializable
    data object Monitor : Route
}