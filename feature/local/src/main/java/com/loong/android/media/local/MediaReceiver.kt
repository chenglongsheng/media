package com.loong.android.media.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.loong.android.media.common.TLog

/**
 * 媒体广播接收器
 */
class MediaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        TLog.i("onReceive: ${intent.action}, ${intent.dataString}")
        when (intent.action) {
            Intent.ACTION_MEDIA_MOUNTED -> {
                MediaStoreManager.onMounted(intent.dataString)
            }

            else -> {}
        }
    }
}