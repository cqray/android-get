package cn.cqray.android.handle

import android.os.SystemClock
import androidx.lifecycle.Observer

/**
 * [GetHandleDelegate]任务
 * @author Cqray
 */
internal class GetHandleTask : java.io.Serializable {
    /** 任务标识 **/
    var tag: Any? = null

    /** 任务what值 **/
    var what: Int = 0

    /** 开始延时 **/
    var delay: Long = 0

    /** 每次执行时长 **/
    var period: Long = 0

    /** 已执行次数 **/
    var doneCount: Int = 0

    /** 更新时间 **/
    var uptime: Long = SystemClock.uptimeMillis()

    /** 任务回调 **/
    var observer: Observer<Long>? = null

    /** 终止条件 **/
    var condition: Function1<Int, Boolean>? = null
}