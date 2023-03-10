package cn.cqray.android.log

/**
 * [GetLog]日志打印适配器
 * @author Cqray
 */
interface GetLogAdapter {

    /**
     * 打印日志
     * @param level 日志等级[GetLogLevel]
     * @param tag 日志标识
     * @param message 日志内容
     * @param exc 异常信息
     */
    fun print(level: GetLogLevel, tag: String, message: String, exc: Throwable?)
}