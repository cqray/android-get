package cn.cqray.android.handle.task

import android.os.SystemClock
import androidx.lifecycle.Observer
import java.util.concurrent.TimeUnit

/**
 * 任务委托基础实现
 * @author Cqray
 */
internal abstract class GetTaskBaseDelegate {

    /** 上一次使用的tag **/
    private var lastTag = 0

    /**
     * 资源回收
     */
    abstract fun onCleared()

    /**
     * 结束指定Tag的任务
     */
    abstract fun clearTask(tag: Int)

    /**
     * 执行一次定时任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param timeUnit 时间单位
     */
    abstract fun timerTask(
        observer: Observer<Long>,
        delay: Number = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): Int

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    abstract fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number? = null,
        condition: Function1<Int, Boolean>? = null,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): Int

    /**
     * 创建任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    @Synchronized
    fun createTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number,
        condition: Function1<Int, Boolean>?,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): GetTask {
        return GetTask().apply {
            this.observer = observer
            this.delay = timeUnit.toMillis(delay.toLong())
            this.period = timeUnit.toMillis(period.toLong())
            this.uptime = SystemClock.uptimeMillis() + this.delay
            this.condition = condition
        }
    }

    /**
     * 创建标识
     * @param tag 标识
     */
    @Synchronized
    fun obtainTag(tag: Int? = null): Int = run { tag ?: ++lastTag }
}