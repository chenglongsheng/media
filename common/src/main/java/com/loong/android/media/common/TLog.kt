package com.loong.android.media.common

import android.util.Log

/**
 * 全局log工具，默认取调用类名作为tag
 */
object TLog {

    /**
     * 是否开启日志打印
     */
    var isLoggable: Boolean = true

    /**
     * 日志打印级别
     * @see Log.VERBOSE
     * @see Log.DEBUG
     * @see Log.INFO
     * @see Log.WARN
     * @see Log.ERROR
     * @see Log.ASSERT
     */
    var logLevel: Int = Log.VERBOSE

    private val log = Timber.Forest

    /**
     * 启用log
     */
    internal fun isLoggable(priority: Int) = isLoggable && priority >= logLevel

    /**
     * 注入自定义tag
     */
    fun tag(tag: String) = log.tag(tag).let { this }

    fun v(message: String?, vararg args: Any?) {
        log.v(message, *args)
    }

    fun v(t: Throwable?, message: String?, vararg args: Any?) {
        log.v(t, message, *args)
    }

    fun v(t: Throwable?) {
        log.v(t)
    }

    fun d(message: String?, vararg args: Any?) {
        log.d(message, args)
    }

    fun d(t: Throwable?, message: String?, vararg args: Any?) {
        log.d(t, message, *args)
    }

    fun d(t: Throwable?) {
        log.d(t)
    }

    fun i(message: String?, vararg args: Any?) {
        log.i(message, *args)
    }

    fun i(t: Throwable?, message: String?, vararg args: Any?) {
        log.i(t, message, *args)
    }

    fun i(t: Throwable?) {
        log.i(t)
    }

    fun w(message: String?, vararg args: Any?) {
        log.w(message, *args)
    }

    fun w(t: Throwable?, message: String?, vararg args: Any?) {
        log.w(t, message, *args)
    }

    fun w(t: Throwable?) {
        log.w(t)
    }

    fun e(message: String?, vararg args: Any?) {
        log.e(message, *args)
    }

    fun e(t: Throwable?, message: String?, vararg args: Any?) {
        log.e(t, message, *args)
    }

    fun e(t: Throwable?) {
        log.e(t)
    }

    fun wtf(message: String?, vararg args: Any?) {
        log.wtf(message, *args)
    }

    fun wtf(t: Throwable?, message: String?, vararg args: Any?) {
        log.wtf(t, message, *args)
    }

    fun wtf(t: Throwable?) {
        log.wtf(t)
    }

    fun log(priority: Int, message: String?, vararg args: Any?) {
        log.log(priority, message, *args)
    }

    fun log(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
        log.log(priority, t, message, *args)
    }

    fun log(priority: Int, t: Throwable?) {
        log.log(priority, t)
    }

}