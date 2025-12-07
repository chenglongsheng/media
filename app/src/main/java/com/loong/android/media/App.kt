package com.loong.android.media

import android.app.Application
import com.loong.android.media.common.MediaManager
import com.loong.android.media.player.PlayController

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MediaManager.instance.init(this)
        PlayController.init(this)
    }

}