package cn.cqray.android.handle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.cqray.android.Get
import java.util.concurrent.TimeUnit

/**
 * [Get]一些定时任务处理委托实现
 * @author Cqray
 */
class GetHandleDelegate constructor(provider: GetHandleProvider? = null) {

    /** [GetTaskDelegate] **/
    private val taskDelegate = GetTaskDelegate()

    /** [GetRxDelegate] **/
    private val rxDelegate = GetRxDelegate()

    init {
        if (provider is LifecycleOwner) {
            // 订阅生命周期，自动回收资源
            provider.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    onCleared()
                }
            })
        }
    }

    /**
     * 资源回收
     */
    fun onCleared() {
        // 清空所有任务
        taskDelegate.onCleared()
        rxDelegate.clearDisposables()
    }

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
    ): Int = taskDelegate.timerTask(observer, delay, timeUnit)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    @JvmOverloads
    fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number? = null,
        condition: Function1<Int, Boolean>? = null,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): Int = taskDelegate.periodicTask(observer, delay, period, condition, timeUnit)

    /**
     * 清除指定tag的[timerTask]、[periodicTask]任务
     * @param tag 指定tag
     */
    fun clearTask(tag: Int) = taskDelegate.clearTask(tag)

    /**
     * 清除所有的[timerTask]、[periodicTask]任务
     */
    fun clearTasks() = taskDelegate.onCleared()

    /**
     * 添加Disposable
     * @param disposable Disposable
     */
    fun addDisposable(disposable: Any) = rxDelegate.addDisposable(disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    @Synchronized
    fun addDisposable(tag: Any?, disposable: Any) = rxDelegate.addDisposable(tag, disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    fun addDisposables(vararg disposables: Any) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    @Synchronized
    fun addDisposables(tag: Any?, vararg disposables: Any) = rxDelegate.addDisposables(tag, *disposables)

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    fun addDisposables(disposables: MutableList<Any>) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    @Synchronized
    fun addDisposables(tag: Any?, disposables: MutableList<Any>) = rxDelegate.addDisposables(tag, disposables)

    /**
     * 根据标识获取对应的列表
     * @param tag 标识
     */
    @Synchronized
    fun getDisposables(tag: Any? = null) = rxDelegate.getDisposables(tag)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    @Synchronized
    fun clearDisposables(tag: Any?) = rxDelegate.clearDisposables(tag)

    /**
     * 清除所有Disposable
     */
    fun clearDisposables() = rxDelegate.clearDisposables()
}