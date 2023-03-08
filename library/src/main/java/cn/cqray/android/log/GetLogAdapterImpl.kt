package cn.cqray.android.log

import android.util.Log
import com.orhanobut.logger.Logger

/**
 * [GetLog]日志打印适配器默认实现
 * @author Cqray
 */
internal class GetLogAdapterImpl : GetLogAdapter {

    /** 是否支持[Logger] **/
    private var loggerSupported = true

    /**
     * 打印日志
     * @param level 日志等级[GetLogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param exc 异常信息
     */
    override fun print(level: GetLogLevel, tag: String, message: String, exc: Throwable?) {
        // 使用Logger
        if (loggerSupported) printByLogger(level, tag, message, exc)
        // 使用内置
        else printByInner(level, tag, message, exc)
    }

    /**
     * [Logger]框架打印
     * @param level 日志等级[GetLogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param tr 异常信息
     */
    @Synchronized
    private fun printByLogger(level: GetLogLevel, tag: String, message: String, tr: Throwable?) {
        try {
            val printer = Logger.t(tag)
            when (level) {
                GetLogLevel.V -> printer.v(message)
                GetLogLevel.D -> printer.d(message)
                GetLogLevel.I -> printer.i(message)
                GetLogLevel.W -> printer.w(message)
                GetLogLevel.E -> printer.e(tr, message)
                else -> printer.wtf(message)
            }
        } catch (e: NoClassDefFoundError) {
            loggerSupported = false
            printByInner(level, tag, message, tr)
        }
    }

    /**
     * 内置打印
     * @param level 日志等级[GetLogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param tr 异常信息
     */
    @Synchronized
    private fun printByInner(level: GetLogLevel, tag: String, message: String, tr: Throwable?) {
        // 打印日志
        when (level) {
            GetLogLevel.V -> Log.v(tag, message)
            GetLogLevel.D -> Log.d(tag, message)
            GetLogLevel.I -> Log.i(tag, message)
            GetLogLevel.W -> Log.w(tag, message)
            GetLogLevel.E -> Log.e(tag, message, tr)
            else -> Log.wtf(tag, message, tr)
        }
    }
}