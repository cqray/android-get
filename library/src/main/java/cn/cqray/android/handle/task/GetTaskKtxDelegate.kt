package cn.cqray.android.handle.task

import android.util.SparseArray
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * 协程任务委托实现
 * @author Cqray
 */
@OptIn(DelicateCoroutinesApi::class)
internal class GetTaskKtxDelegate : GetTaskBaseDelegate() {

    /** [Job]缓存 **/
    private val jobs = SparseArray<Job>()

    override fun onCleared() {
        // 释放job缓存
        synchronized(jobs) {
            for (i in 0 until jobs.size()) {
                jobs.valueAt(i)?.cancel()
            }
            jobs.clear()
        }
    }

    @Synchronized
    override fun clearTask(tag: Int): Unit = run { jobs.get(tag)?.cancel() }

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
            synchronized(jobs) { jobs.get(it)?.cancel() }
        }
        // 执行任务
        val job = GlobalScope.launch(Dispatchers.IO) {
            // 创建任务
            val task = createTask(observer, delay, period ?: delay, condition, timeUnit)
            // 延时执行
            delay(timeUnit.toMillis(task.delay))
            // 执行次数加1
            task.doneCount += 1
            // 处理回调
            withContext(Dispatchers.Main) { task.observer.onChanged(task.doneCount.toLong()) }
            // 更新时间累加
            task.uptime += task.period
            // 中断条件未满足，则一直执行
            while (task.condition?.invoke(task.doneCount) != true) {
                // 延时等待
                delay(timeUnit.toMillis(task.period))
                // 执行次数加1
                task.doneCount += 1
                // 处理回调
                withContext(Dispatchers.Main) { task.observer.onChanged(task.doneCount.toLong()) }
                // 更新时间累加
                task.uptime += task.period
            }
            // 取消任务
            synchronized(jobs) {
                jobs.get(tag)?.cancel()
                jobs.remove(tag)
            }
        }
        // 缓存任务
        synchronized(jobs) { jobs.put(tag, job) }
        // 返回tag
        return tag
    }
}