package cn.cqray.android.log

import android.util.Log
import com.orhanobut.logger.Logger

/**
 * [GetLog]日志打印适配器默认实现
 * @author Cqray
 */
internal class LogAdapterImpl : LogAdapter {

    /** 是否支持[Logger] **/
    private var supportLogger = true

    /**
     * 打印日志
     * @param level 日志等级[LogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param exc 异常信息
     */
    override fun print(level: LogLevel, tag: String, message: String, exc: Throwable?) {
        // 使用Logger
        if (supportLogger) printByLogger(level, tag, message, exc)
        // 使用内置
        else printByInner(level, tag, message, exc)
    }

    /**
     * [Logger]框架打印
     * @param level 日志等级[LogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param tr 异常信息
     */
    @Synchronized
    private fun printByLogger(level: LogLevel, tag: String, message: String, tr: Throwable?) {
        try {
            val printer = Logger.t(tag)
            when (level) {
                LogLevel.V -> printer.v(message)
                LogLevel.D -> printer.d(message)
                LogLevel.I -> printer.i(message)
                LogLevel.W -> printer.w(message)
                LogLevel.E -> printer.e(tr, message)
                else -> printer.wtf(message)
            }
        } catch (e: NoClassDefFoundError) {
            supportLogger = false
            printByInner(level, tag, message, tr)
        }
    }

    /**
     * 内置打印
     * @param level 日志等级[LogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param tr 异常信息
     */
    @Synchronized
    private fun printByInner(level: LogLevel, tag: String, message: String, tr: Throwable?) {
        // 打印日志
        when (level) {
            LogLevel.V -> Log.v(tag, message)
            LogLevel.D -> Log.d(tag, message)
            LogLevel.I -> Log.i(tag, message)
            LogLevel.W -> Log.w(tag, message)
            LogLevel.E -> Log.e(tag, message, tr)
            else -> Log.wtf(tag, message, tr)
        }
    }
}