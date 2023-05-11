package cn.cqray.android.log

/**
 * [GetLog]日志打印适配器
 * @author Cqray
 */
interface GetLogAdapter : java.io.Serializable {

    /**
     * 打印文件日志
     * @param level 日志等级[GetLog.Level]
     * @param tag 日志标识
     * @param content 日志内容
     */
    fun file(@GetLog.Level level: Int, tag: String?, content: String?)

    /**
     * 打印Json日志
     * @param level 日志等级[GetLog.Level]
     * @param tag 日志标识
     * @param content 日志内容
     */
    fun json(@GetLog.Level level: Int, tag: String?, content: String?)

    /**
     * 打印XML日志
     * @param level 日志等级[GetLog.Level]
     * @param tag 日志标识
     * @param content 日志内容
     */
    fun xml(@GetLog.Level level: Int, tag: String?, content: String?)

    /**
     * 打印日志
     * @param level 日志等级[GetLog.Level]
     * @param tag 日志标识
     * @param contents 日志内容
     */
    fun print(@GetLog.Level level: Int, tag: String?, vararg contents: Any?)
}