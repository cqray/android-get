package cn.cqray.android.handle.task

import android.os.SystemClock
import androidx.lifecycle.Observer

/**
 * 任务
 * @author Cqray
 */
class GetTask : java.io.Serializable {

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
    lateinit var observer: Observer<Long>

    /** 终止条件 **/
    var condition: Function1<Int, Boolean>? = null
}