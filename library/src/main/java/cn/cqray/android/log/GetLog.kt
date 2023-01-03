package cn.cqray.android.log

import cn.cqray.android.Get

/**
 * [Get]日志
 * @author Cqray
 */
@Suppress("unused")
object GetLog {

    @JvmStatic
    fun v(message: String?) = print(LogLevel.V, null, message, null)

    @JvmStatic
    fun v(tag: Any?, message: String?) = print(LogLevel.V, tag, message, null)

    @JvmStatic
    fun d(message: String?) = print(LogLevel.D, null, message, null)

    @JvmStatic
    fun d(tag: Any?, message: String?) = print(LogLevel.D, tag, message, null)

    @JvmStatic
    fun i(message: String?) = print(LogLevel.I, null, message, null)

    @JvmStatic
    fun i(tag: Any?, message: String?) = print(LogLevel.I, tag, message, null)

    @JvmStatic
    fun w(message: String?) = print(LogLevel.W, null, message, null)

    @JvmStatic
    fun w(tag: Any?, message: String?) = print(LogLevel.W, tag, message, null)

    @JvmStatic
    fun e(text: String?) = print(LogLevel.E, null, text, null)

    @JvmStatic
    fun e(tag: Any?, text: String?) = print(LogLevel.E, tag, text, null)

    @JvmStatic
    fun e(tag: Any?, text: String?, exc: Throwable?) = print(LogLevel.E, tag, text, exc)

    @JvmStatic
    fun wtf(message: String?) = print(LogLevel.WTF, null, message, null)

    @JvmStatic
    fun wtf(tag: Any?, message: String?) = print(LogLevel.WTF, tag, message, null)

    @JvmStatic
    fun print(level: LogLevel?, tag: Any?, message: String?, tr: Throwable?) {
        // 获取配置
        val logInit = Get.init.logInit!!
        val logTag = logInit.tag!!
        val logLevel = logInit.logLevel!!
        val logAdapter = logInit.logAdapter!!
        // 判断日志是否需要打印
        if (logLevel == LogLevel.NONE) return
        if ((level?.ordinal ?: 0) < logLevel.ordinal) return
        // 无需要打印的内容
        if (message == null && tr == null) return
        // 获取当前日志标识
        val newTag = logTag + when (tag) {
            null -> ""
            is String -> "-$tag"
            is Class<*> -> "-${tag.simpleName}"
            else -> "-${tag.javaClass.simpleName}"
        }
        // 获取日志内容
        val newMessage = message ?: if (level != LogLevel.E) tr!!.message else ""
        // 打印日志
        logAdapter.print(level ?: LogLevel.V, newTag, newMessage ?: "", tr)
    }
}