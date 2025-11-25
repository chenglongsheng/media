package com.loong.android.media.local

import androidx.annotation.IntDef

/**
 * 存储卷状态
 */
@IntDef(
    value = [
        VolumeState.STATE_NO_PERMISSIONS,
        VolumeState.STATE_SCANNING,
        VolumeState.STATE_DATA_LIST,
        VolumeState.STATE_DATA_EMPTY,
    ]
)
annotation class VolumeState {
    companion object {
        /**
         * 无权限
         */
        const val STATE_NO_PERMISSIONS = 0

        /**
         * 扫描中
         */
        const val STATE_SCANNING = 1

        /**
         * 扫描完成
         */
        const val STATE_DATA_LIST = 2

        /**
         * 扫描完成，但数据为空
         */
        const val STATE_DATA_EMPTY = 3
    }
}
