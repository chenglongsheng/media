package com.loong.android.media

import android.app.Application
import com.loong.android.media.player.PlayManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PlayManager.init(this)
    }

}