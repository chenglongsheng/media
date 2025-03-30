package com.loong.android.media

import android.app.Application
import com.loong.android.media.common.TLog
import com.loong.android.media.player.PlayManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TLog.i("onCreate: ")
        PlayManager.init(this)
    }

}