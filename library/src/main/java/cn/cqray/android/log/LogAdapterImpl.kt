package cn.cqray.android.log

import cn.cqray.android.Get
import com.blankj.utilcode.util.LogUtils

/**
 * [GetLog]日志打印适配器默认实现
 * @author Cqray
 */
internal class LogAdapterImpl : LogAdapter {

    override fun print(@GetLog.Level level: Int, tag: String?, vararg contents: Any?) {
        initLogConfig()
        LogUtils.log(level, tag, *contents)
    }

    override fun file(@GetLog.Level level: Int, tag: String?, content: String?) {
        initLogConfig()
        val type = 0x10 or level
        LogUtils.log(type, tag, content)
    }

    override fun json(@GetLog.Level level: Int, tag: String?, content: String?) {
        initLogConfig()
        val type = 0x20 or level
        LogUtils.log(type, tag, content)
    }

    override fun xml(@GetLog.Level level: Int, tag: String?, content: String?) {
        initLogConfig()
        val type = 0x30 or level
        LogUtils.log(type, tag, content)
    }

    private fun initLogConfig() {
        val init = Get.init.logInit!!
        LogUtils.getConfig()
            // 设置 log 总开关，包括输出到控制台和文件，默认开
            .setLogSwitch(init.isLogEnable)
            // 设置 log 全局标签，默认为空
            // 当全局标签不为空时，我们输出的 log 全部为该 tag，
            // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            .setGlobalTag(init.globalTag)
            // 设置 log 头信息开关，默认为开
            .setLogHeadSwitch(init.isLogHeadEnable)
            // 输出日志是否带边框开关，默认开
            .setBorderSwitch(init.isBorderEnable)
            // log 栈深度，默认为 1
            .setStackDeep(init.stackDeep)
            // 设置栈偏移，比如二次封装的话就需要设置，默认为 0
            .setStackOffset(init.stackOffset + 2)
            //===========================================
            //===============Logcat相关配置===============
            //===========================================
            // 设置是否输出到控制台开关，默认开
            .setConsoleSwitch(init.isLogcatEnable)
            // 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setSingleTagSwitch(init.isLogcatAS31Enable)
            // log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setConsoleFilter(init.logcatLevel)
            //===========================================
            //===============日志文件相关配置==============
            //===========================================
            // 打印 log 时是否存到文件的开关，默认关
            .setLog2FileSwitch(init.isLogFileEnable)
            // log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(init.fileLevel)
            // 当自定义路径为空时，写入应用的/cache/log/目录中
            .setDir(init.fileDir)
            // 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
            .setFilePrefix(init.filePrefix)
            // 设置日志文件后缀
            .setFileExtension(init.fileExtension)
            // 设置日志可保留天数，默认为 -1 表示无限时长
            .setSaveDays(init.fileSaveDays)
//            // 日志文件头部
//            .addFileExtraHead(init.fileExtraHead)
//            // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
//            .addFormatter(object : LogUtils.IFormatter<ArrayList>() {
//                override fun format(arrayList: ArrayList): String {
//                    return "LogUtils Formatter ArrayList { " + arrayList.toString() + " }"
//                }
//            })
    }
}