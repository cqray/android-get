package cn.cqray.android.handle.task

import android.util.SparseArray
import androidx.lifecycle.Observer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * RxJava3任务委托实现
 * @author Cqray
 */
internal class GetTaskRx3Delegate : GetTaskBaseDelegate() {

    /** [Disposable]缓存 **/
    private val disposables = SparseArray<Disposable>()

    override fun onCleared() {
        // 释放job缓存
        synchronized(disposables) {
            for (i in 0 until disposables.size()) {
                disposables.valueAt(i)?.dispose()
            }
            disposables.clear()
        }
    }

    @Synchronized
    override fun clearTask(tag: Int): Unit = run { disposables.get(tag)?.dispose() }

    /**
     * 执行一次定时任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param timeUnit 时间单位
     */
    override fun timerTask(
        observer: Observer<Long>,
        delay: Number,
        timeUnit: TimeUnit
    ): Int = periodicTask(observer, delay, delay, { it == 1 }, timeUnit)

    /**
     * 周期性地执行任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    override fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number?,
        condition: Function1<Int, Boolean>?,
        timeUnit: TimeUnit
    ): Int {
        // 获取标签
        val tag = obtainTag().also {
            // 如果之前的任务存在，则取消
            synchronized(disposables) { disposables.get(it)?.dispose() }
        }
        // 创建任务
        val task = createTask(observer, delay, period ?: delay, condition, timeUnit)
        val d = Observable.interval(task.delay, task.period, timeUnit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // 执行次数加1
                task.doneCount += 1
                // 处理回调
                task.observer.onChanged(task.doneCount.toLong())
                // 更新时间累加
                task.uptime += task.period
                // 是否满足中断条件
                if (task.condition?.invoke(task.doneCount) == true) {
                    // 取消任务
                    synchronized(disposables) {
                        disposables.get(tag)?.dispose()
                        disposables.remove(tag)
                    }
                }
            }
        // 缓存任务
        synchronized(disposables) { disposables.put(tag, d) }
        // 返回tag
        return tag
    }
}