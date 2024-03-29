package cn.cqray.android.log

import cn.cqray.android.app.GetBaseInit

/**
 * [GetLog]配置
 * @author Cqray
 */
class GetLogInit : GetBaseInit() {

    /** 日志适配器 **/
    var adapterGet: Function0<GetLogAdapter> = { GetLogAdapterImpl() }

    //===========================================
    //===============Log打印相关配置===============
    //===========================================

    /** 是否启用日志 **/
    var isLogEnable: Boolean = true

    /** 日志全局标识 **/
    var globalTag: String = "Get"

    /** 是否启用日志头 **/
    var isLogHeadEnable: Boolean = true

    /** 是否启用边框 **/
    var isBorderEnable: Boolean = true

    /** 栈深度 **/
    var stackDeep: Int = 1

    /** 栈偏移 **/
    var stackOffset: Int = 0

    //===========================================
    //===============Logcat相关配置===============
    //===========================================

    /** 是否启用输出到Logcat控制台 **/
    var isLogcatEnable: Boolean = true

    /** 是否启用美化AS3.1的Logcat。一条日志仅输出一条 **/
    var isLogcatAS31Enable: Boolean = false

    /** Logcat控制台日志等级 **/
    @GetLog.Level
    var logcatLevel: Int = GetLog.V

    //===========================================
    //===============日志文件相关配置==============
    //===========================================

    /** 是否启用日志写入文件开关 **/
    var isLogFileEnable: Boolean = false

    /** 文件日志等级 **/
    @GetLog.Level
    var fileLevel: Int = GetLog.V

    /** 文件路径。为空时写入应用的/cache/log/目录中 **/
    var fileDir: String = ""

    /** 文件前缀。当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension" **/
    var filePrefix: String = "Get"

    /** 日志文件后缀 **/
    var fileExtension: String = ".log"

    /** 日志文件可保留天数，默认为 -1 表示无限时长 **/
    var fileSaveDays: Int = 3

}