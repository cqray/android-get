package cn.cqray.android.handle

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.cqray.android.Get
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * [Get]一些定时任务处理委托实现
 * @author Cqray
 */
class GetHandleDelegate constructor(provider: GetHandleProvider? = null) {

    /** 当前What **/
    private var currentWhat = Int.MIN_VALUE

    /** [Handler]处理器 **/
    private val handler = Handler(Looper.getMainLooper(), ::onHandleTask)

    /** 任务列表 **/
    private val tasks = Collections.synchronizedList(mutableListOf<GetHandleTask>())

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
        rxDelegate.clearDisposables()
        // 清空所有任务
        clearTasks()
        // gc回收
        System.gc()
    }

    /**
     * 处理[timerTask]、[periodicTask]任务
     * @param message 消息内容
     */
    private fun onHandleTask(message: Message): Boolean {
        // 获取任务信息
        val task = message.obj as? GetHandleTask
        // 初始消息
        task?.let {
            // 执行次数加1
            task.doneCount += 1
            // 处理回调
            task.observer?.onChanged(task.doneCount.toLong())
            // 继续与否条件判断
            if (task.condition?.invoke(task.doneCount) == true) {
                // 任务终止
                handler.removeMessages(task.what)
                tasks.remove(task)
            } else {
                // 更新时间累加
                task.uptime += task.period
                // 生成消息内容
                val newMessage = obtainMessage(task, message.what)
                // 定时发送消息
                handler.sendMessageAtTime(newMessage, task.uptime)
            }
        }
        return true
    }

    /**
     * 执行一次定时任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param timeUnit 时间单位
     */
    @JvmOverloads
    @Synchronized
    fun timerTask(observer: Observer<Long>, delay: Number = 0, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): Int {
        // 创建任务
        val task = createTask(observer, delay, 0, { it == 1 }, timeUnit)
        // 生成消息内容
        val message = obtainMessage(task, null)
        // 定时发送信息
        handler.sendMessageAtTime(message, task.uptime)
        // 返回what值
        return message.what
    }

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
    ): Int {
        // 创建任务
        val task = createTask(observer, delay, period ?: delay, condition, timeUnit)
        // 生成消息内容
        val message = obtainMessage(task, null)
        // 定时发送信息
        handler.sendMessageAtTime(message, task.uptime)
        // 返回what值
        return message.what
    }

    /**
     * 清除指定what的[timerTask]、[periodicTask]任务
     * @param what 指定what
     */
    @Synchronized
    fun clearTask(what: Int) {
        val task = this.tasks.find { it.what == what }
        task?.let {
            handler.removeMessages(it.what)
            tasks.remove(it)
        }
    }

    /**
     * 清除所有的[timerTask]、[periodicTask]任务
     */
    @Synchronized
    fun clearTasks() {
        tasks.forEach { handler.removeMessages(it.what) }
        tasks.clear()
    }

    /**
     * 创建任务
     * @param observer 观察者
     * @param delay 延时时间
     * @param period 间隔时间
     * @param condition 终止条件
     * @param timeUnit 时间单位
     */
    @Synchronized
    private fun createTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number,
        condition: Function1<Int, Boolean>?,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): GetHandleTask {
        return GetHandleTask().apply {
            this.observer = observer
            this.delay = timeUnit.toMillis(delay.toLong())
            this.period = timeUnit.toMillis(period.toLong())
            this.uptime = SystemClock.uptimeMillis() + this.delay
            this.condition = condition
        }
    }

    /**
     * 创建消息
     * @param task 任务
     * @param what 类型
     */
    @Synchronized
    private fun obtainMessage(task: GetHandleTask, what: Int?): Message {
        val newWhat = what ?: ++currentWhat
        // 生成消息内容
        return Message.obtain().also {
            it.obj = task.also { t -> t.what = newWhat }
            it.what = newWhat
            tasks.add(task)
        }
    }

    /**
     * 添加Disposable
     * @param disposable Disposable
     */
    fun addDisposable(disposable: Disposable) = rxDelegate.addDisposable(disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    @Synchronized
    fun addDisposable(tag: Any?, disposable: Disposable) = rxDelegate.addDisposable(tag, disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    fun addDisposables(vararg disposables: Disposable) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    @Synchronized
    fun addDisposables(tag: Any?, vararg disposables: Disposable) = rxDelegate.addDisposables(tag, *disposables)

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    fun addDisposables(disposables: MutableList<Disposable>) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    @Synchronized
    fun addDisposables(tag: Any?, disposables: MutableList<Disposable>) = rxDelegate.addDisposables(tag, disposables)

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