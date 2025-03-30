package com.loong.android.media.common

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * 控制台日志输入实现
 *
 * @param tagPrefix tag前缀
 * @param msgPrefix 日志信息前缀
 */
class ConsoleLoggerTree(
    private val tagPrefix: String,
    private val msgPrefix: String = ""
) : Timber.DebugTree() {

    /**
     * logger是否初始化
     */
    @Volatile
    private var hasInitLogger = false

    /**
     * 初始化Logger和配置
     */
    @Synchronized
    private fun initLogger() {
        if (!hasInitLogger) {
            hasInitLogger = true
            val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
                .methodCount(1)
                .methodOffset(5)
                .tag(tagPrefix)
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        }
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return TLog.isLoggable(priority)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (usePretty) {
            if (!hasInitLogger) {
                initLogger()
            }
            val text = message.trim()
            if (text.startsWith("{") || text.startsWith("[")) {
                Logger.json(text)
            } else {
                Logger.log(priority, tag, message, t)
            }
        } else {
            super.log(priority, "$tagPrefix-$tag", "$msgPrefix$message", t)
        }
    }

    companion object {
        /**
         * 启用日志美化
         */
        var usePretty: Boolean = BuildConfig.DEBUG
    }
}