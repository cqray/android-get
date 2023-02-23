package cn.cqray.android.handle

import android.os.SystemClock

/**
 * [GetHandleDelegate]任务
 * @author Cqray
 */
internal class GetHandleTask : java.io.Serializable {
    /** 任务标识 **/
    var tag: Any? = null

    /** 每次执行时长 **/
    var duration: Int = 0

    /** 需要执行次数 **/
    var execCount: Int? = null

    /** 已执行次数 **/
    var doneCount: Int = 0

    /** 更新时间 **/
    var uptime: Long = SystemClock.uptimeMillis()

    /** 任务回调 **/
    var func: (() -> Unit)? = null

    /** 终止条件 **/
    var condition: ((Int) -> Boolean)? = null
}