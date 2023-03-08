package cn.cqray.android.log

import android.app.Activity
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * [GetLog]配置
 * @author Cqray
 */
class GetLogInit : Serializable {

    /** 日志全局标识 **/
    var tag: String = "Get"

    /** 启用[Activity]生命周期日志 **/
    var activityLifecycleLogEnable: Boolean = true

    /** 启用[Fragment]生命周期日志 **/
    var fragmentLifecycleLogEnable: Boolean = true

    /** 日志等级 **/
    var logLevel: GetLogLevel = GetLogLevel.V

    /** 日志适配器 **/
    var logAdapter: GetLogAdapter = GetLogAdapterImpl()
}