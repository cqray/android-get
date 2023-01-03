package cn.cqray.android.log

import android.app.Activity
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * [GetLog]配置
 * @author Cqray
 */
class LogInit : Serializable {

    /** 日志全局标识 **/
    var tag: String? = "Get"
        set(tag) {
            field = tag ?: "Get"
        }

    /** 启用[Activity]生命周期日志 **/
    var activityLifecycleLogEnable: Boolean? = true
        set(enable) {
            field = enable ?: true
        }

    /** 启用[Fragment]生命周期日志 **/
    var fragmentLifecycleLogEnable: Boolean? = true
        set(enable) {
            field = enable ?: true
        }

    /** 日志等级 **/
    var logLevel: LogLevel? = LogLevel.V
        set(level) {
            field = level ?: LogLevel.V
        }

    /** 日志适配器 **/
    var logAdapter: LogAdapter? = LogAdapterImpl()
        set(adapter) {
            field = adapter ?: LogAdapterImpl()
        }
}