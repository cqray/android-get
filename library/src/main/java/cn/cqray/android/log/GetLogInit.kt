package cn.cqray.android.log

import android.app.Activity
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * [GetLog]配置
 * @author Cqray
 */
class GetLogInit : Serializable {

    /** 启用[Activity]生命周期日志 **/
    var activityLifecycleLogEnable: Boolean = true

    /** 启用[Fragment]生命周期日志 **/
    var fragmentLifecycleLogEnable: Boolean = true

    /** 日志适配器 **/
    var logAdapter: GetLogAdapter = GetLogAdapterImpl()

    //===========================================
    //===============Log打印相关配置===============
    //===========================================

    /** 是否启用日志 **/
    var isLogEnable = true

    /** 日志全局标识 **/
    var globalTag: String = "Get"

    /** 是否启用日志头 **/
    var isLogHeadEnable = true

    /** 是否启用边框 **/
    var isBorderEnable = true

    /** 栈深度 **/
    var stackDeep = 1

    /** 栈偏移 **/
    var stackOffset = 0

    //===========================================
    //===============Logcat相关配置===============
    //===========================================

    /** 是否启用输出到Logcat控制台 **/
    var isLogcatEnable = true

    /** 是否启用美化AS3.1的Logcat。一条日志仅输出一条 **/
    var isLogcatAS31Enable = false

    /** Logcat控制台日志等级 **/
    @GetLog.Level
    var logcatLevel = GetLog.V

    //===========================================
    //===============日志文件相关配置==============
    //===========================================

    /** 是否启用日志写入文件开关 **/
    var isLogFileEnable = false

    /** 文件日志等级 **/
    @GetLog.Level
    var fileLevel = GetLog.V

    /** 文件路径。为空时写入应用的/cache/log/目录中 **/
    var fileDir = ""

    /** 文件前缀。当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension" **/
    var filePrefix = "Get"

    /** 日志文件后缀 **/
    var fileExtension = ".log"

    /** 日志文件可保留天数，默认为 -1 表示无限时长 **/
    var fileSaveDays = 3

    /** 日志文件头部 **/
    var fileExtraHead: Map<String,String> = mapOf()

}