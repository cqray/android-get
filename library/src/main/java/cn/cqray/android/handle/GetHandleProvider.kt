package cn.cqray.android.handle

import androidx.lifecycle.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * [GetHandleDelegate]提供器
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetHandleProvider {

    /**
     *  获取并初始化[GetHandleProvider]
     */
    val handleDelegate: GetHandleDelegate

    /**
     * 执行一次定时任务
     * @param observer 观察者
     */
    fun timerTask(observer: Observer<Long>) = handleDelegate.timerTask(observer, 0)

    /**
     * 执行一次定时任务
     * @param observer 观察者
     * @param delay 延时时间
     */
    fun timerTask(observer: Observer<Long>, delay: Number) = handleDelegate.timerTask(observer, delay)

    /**
     * 执行一次定时任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param timeUnit 时间单位
     */
    fun timerTask(
        observer: Observer<Long>,
        delay: Number,
        timeUnit: TimeUnit
    ) = handleDelegate.timerTask(observer, delay, timeUnit)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param period 间隔时间
     */
    fun periodicTask(
        observer: Observer<Long>,
        period: Number,
    ) = handleDelegate.periodicTask(observer, period)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     */
    fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number? = null
    ) = handleDelegate.periodicTask(observer, delay, period)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     */
    fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number? = null,
        condition: Function1<Int, Boolean>?
    ) = handleDelegate.periodicTask(observer, delay, period, condition)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number? = null,
        condition: Function1<Int, Boolean>?,
        timeUnit: TimeUnit
    ) = handleDelegate.periodicTask(observer, delay, period, condition, timeUnit)

    /**
     * 清除指定what的[timerTask]、[periodicTask]任务
     * @param what 指定what
     */
    fun clearTasks(what: Int) = handleDelegate.clearTask(what)

    /**
     * 清除所有的[timerTask]、[periodicTask]任务
     */
    fun clearTasks() = handleDelegate.clearTasks()

    //================================================
    //=================Disposable部分==================
    //================================================

    /**
     * 添加Disposable
     * @param disposable Disposable
     */
    fun addDisposable(disposable: Disposable) = handleDelegate.addDisposable(disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    fun addDisposable(tag: Any?, disposable: Disposable) = handleDelegate.addDisposable(tag, disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    fun addDisposables(vararg disposables: Disposable) = handleDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    fun addDisposables(tag: Any?, vararg disposables: Disposable) = handleDelegate.addDisposables(tag, *disposables)

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    fun addDisposables(disposables: MutableList<Disposable>) = handleDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    fun addDisposables(tag: Any?, disposables: MutableList<Disposable>) = handleDelegate.addDisposables(tag, disposables)

    /**
     * 根据标识获取对应的列表
     * @param tag 标识
     */
    fun getDisposables(tag: Any? = null) = handleDelegate.getDisposables(tag)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    fun clearDisposables(tag: Any?) = handleDelegate.clearDisposables(tag)

    /**
     * 清除所有Disposable
     */
    fun clearDisposables() = handleDelegate.clearDisposables()
}