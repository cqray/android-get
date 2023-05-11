package cn.cqray.android.handle.task

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * [Handler]任务委托实现
 * @author Cqray
 */
internal class GetTaskHandleDelegate : GetTaskBaseDelegate() {

    /** [Handler]处理器 **/
    private val handler = Handler(Looper.getMainLooper(), ::onHandleTask)

    /** 任务列表 **/
    private val tasks = Collections.synchronizedList(mutableListOf<GetTask>())

    override fun onCleared() {
        tasks.forEach { handler.removeMessages(it.what) }
        tasks.clear()
    }

    override fun clearTask(tag: Int) = handler.removeMessages(tag)

    /**
     * 处理[timerTask]、[periodicTask]任务
     * @param message 消息内容
     */
    private fun onHandleTask(message: Message): Boolean {
        // 获取任务信息
        val task = message.obj as? GetTask
        // 初始消息
        task?.let {
            // 执行次数加1
            task.doneCount += 1
            // 处理回调
            task.observer.onChanged(task.doneCount.toLong())
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
    override fun timerTask(observer: Observer<Long>, delay: Number, timeUnit: TimeUnit): Int {
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
    override fun periodicTask(
        observer: Observer<Long>,
        delay: Number,
        period: Number?,
        condition: Function1<Int, Boolean>?,
        timeUnit: TimeUnit
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
     * 创建消息
     * @param task 任务
     * @param what 类型
     */
    @Synchronized
    private fun obtainMessage(task: GetTask, what: Int?): Message {
        val newWhat = obtainTag(what)
        // 生成消息内容
        return Message.obtain().also {
            it.obj = task.also { t -> t.what = newWhat }
            it.what = newWhat
            tasks.add(task)
        }
    }
}