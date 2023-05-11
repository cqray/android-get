package cn.cqray.android.handle

import androidx.lifecycle.Observer
import cn.cqray.android.handle.task.GetTaskBaseDelegate
import cn.cqray.android.handle.task.GetTaskHandleDelegate
import cn.cqray.android.handle.task.GetTaskKtxDelegate
import cn.cqray.android.handle.task.GetTaskRx2Delegate
import cn.cqray.android.handle.task.GetTaskRx3Delegate
import cn.cqray.android.util.Check3rdUtils
import java.util.concurrent.TimeUnit

/**
 * 任务委托
 * @author Cqray
 */
class GetTaskDelegate {

    /** 初始化[GetTaskBaseDelegate]实例 **/
    private val delegate: GetTaskBaseDelegate =
        if (Check3rdUtils.isCoroutinesSupport) GetTaskKtxDelegate()
        else if (Check3rdUtils.isRxJava3Support) GetTaskRx3Delegate()
        else if (Check3rdUtils.isRxJava2Support) GetTaskRx2Delegate()
        else GetTaskHandleDelegate()

    /**
     * 执行一次定时任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param timeUnit 时间单位
     */
    @JvmOverloads
    fun timerTask(
        observer: Observer<Long>,
        delay: Number = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): Int = delegate.timerTask(observer, delay, timeUnit)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    @JvmOverloads
    @Synchronized
    fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number? = null,
        condition: Function1<Int, Boolean>? = null,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): Int = delegate.periodicTask(observer, delay, period, condition, timeUnit)

    /** 结束任务 **/
    fun clearTask(tag: Int) = delegate.clearTask(tag)

    /** 任务完结 **/
    fun onCleared() = delegate.onCleared()
}