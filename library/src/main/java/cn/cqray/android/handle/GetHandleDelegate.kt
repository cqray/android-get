package cn.cqray.android.handle

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.log.GetLog

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * [Get]一些定时任务处理委托实现
 * @author Cqray
 */
class GetHandleDelegate constructor(private val provider: GetHandleProvider? = null) {

    /** 当前What **/
    private var currentWhat = Int.MIN_VALUE

    /** 待处理的What类型缓存 **/
    private val handleWhats = Collections.synchronizedMap(HashMap<Int, Any?>())

    /** [Handler]处理器 **/
    private val handler = Handler(Looper.getMainLooper(), ::onHandleTask)

    private val disposableMap = HashMap<Any?, MutableList<Any>>()



//    private val disposableMap = HashMap<Any?, ArrayList<Any?>>()

    init {
        if (provider is LifecycleOwner) {
            // 订阅生命周期，自动回收资源
            provider.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    onCleared()
                }
            })
            handleDelegates[provider] = this
        } else {
            GetLog.d(
                this,
                "You need to manually call onCleared() " +
                        "because you have not bound the LifecycleOwner."
            )
        }
    }

    private fun obtain(tag: Any?): MutableList<Any> {
        // 获取Disposable列表，没有则创建新的
        return disposableMap[tag] ?: mutableListOf<Any>().also { disposableMap[tag] = it }
    }

    /**
     * 资源回收
     */
    fun onCleared() {
        // 清空Handler任务
        handleWhats.clear()
        handler.removeCallbacksAndMessages(null)
        // 从缓存中移除
        if (provider != null) handleDelegates.remove(provider)
        // gc回收
        System.gc()
    }

    /**
     * 处理[timer]、[periodic]任务消息
     * @param message 消息内容
     */
    private fun onHandleTask(message: Message): Boolean {
        // 获取任务信息
        val task = message.obj as GetHandleTask
        // 处理回调
        task.func?.invoke()
        // 执行次数加1
        task.doneCount += 1
        // 继续与否条件判断
        if (task.condition?.invoke(task.doneCount) == true) {
            // 任务终止
            handleWhats.remove(message.what)
        } else {
            // 更新时间累加
            task.uptime += task.duration
            // 生成消息内容
            val newMessage = obtainMessage(task, message.what)
            // 定时发送消息
            handler.sendMessageAtTime(newMessage, task.uptime)
        }
        return true
    }

    /**
     * 执行一次定时任务
     * @param tag       标识
     * @param func      回调
     * @param delayed   延时时间
     */

    fun timer(tag: Any?, func: Function?, delayed: Int?) {
        // 执行一次定时任务
        timer(tag, { func?.invoke() }, delayed)
    }

    /**
     * 执行一次定时任务
     * @param tag       标识
     * @param func      回调
     * @param delayed   延时时间
     */
    @Synchronized
    fun timer(tag: Any?, func: (() -> Unit)?, delayed: Int?) {
        // 创建任务
        val task = createTask(tag, func, delayed) { it == 1 }
        // 生成消息内容
        val message = obtainMessage(task, null)
        // 定时发送信息
        handler.sendMessageAtTime(message, task.uptime)
    }

//    /**
//     * 周期性地执行任务
//     * @param tag       指定标识
//     * @param func      回调
//     * @param delayed   初始延迟时间
//     * @param duration  任务间隔时间
//     * @param doCount   执行次数，null及小于等于0为无数次
//     */
//    @Synchronized
//    fun periodic(tag: Any?, func: GetFunction?, delayed: Int?, duration: Int?, doCount: Int?) {
//        if (func == null) return
//        if (duration == null || duration <= 0) return
//        // 延时执行时间
//        val delay = delayed ?: 0
//        // 延时执行次数
//        val delayCount = if (delay > 0) 1 else 0
//        // 总执行次数
//        val totalCount = if (doCount == null || doCount <= 0) null else doCount
//        // 剩余的执行次数
//        val count = if (totalCount == null) null else totalCount - delayCount
//
//        timer(tag, {
//
//        }, delay)
//
//        if (delay > 0) {
//            // 延时执行第一次
//            periodic(tag, func, null, delay, 1)
//        }
//        // 还有剩余需要执行的任务
//        if (count == null || count > 0) {
//            // 创建任务
//            val task = createTask(tag, func, duration, count)
//            // 因为有延时执行，所以加上延时时间
//            task.uptime += delay
//            // 生成消息内容
//            val message = obtainMessage(task, null)
//            // 定时发送信息
//            handler.sendMessageAtTime(message, task.uptime)
//        }
//    }

    @Synchronized
    fun periodic(
        tag: Any?,
        func: Function?,
        delayed: Int?,
        duration: Int?,
        condition: ((Int) -> Boolean)?,
    ) = periodic(tag, { func?.invoke() }, delayed, duration, condition)

    @Synchronized
    fun periodic(
        tag: Any?,
        func: (() -> Unit)?,
        delayed: Int?,
        duration: Int?,
        condition: ((Int) -> Boolean)?,
    ) {
        // 回调为空不继续执行
        if (func == null) return
        // 周期间隔时间无效，则不继续
        if (duration == null || duration <= 0) return
        // 终止条件为True，不继续执行
        if (condition?.invoke(0) == true) return
        // 延时执行时间
        val delay = delayed ?: 0


//        // 延时执行次数
//        val delayCount = if (delay > 0) 1 else 0
//
//        // 任务是否终止
//        val isTerminate = condition?.invoke(0)

//        timer(tag, func, delayed)

//        if (delay > 0) {
//
//        } else {
//            // 创建任务
//            val task = createTask(tag, func, duration, condition)
//            // 生成消息内容
//            val message = obtainMessage(task, null)
//            // 定时发送信息
//            handler.sendMessageAtTime(message, task.uptime)
//        }


        timer(tag, {

            func.invoke()

            if (condition?.invoke(1) != true) {
                // 创建任务
                val task = createTask(tag, func, duration, condition)
//                // 因为有延时执行，所以加上延时时间
//                task.uptime += delay
                // 生成消息内容
                val message = obtainMessage(task, null)
                // 定时发送信息
                handler.sendMessageAtTime(message, task.uptime)
            }
        }, delay)


//        timer(tag, object : Function {
//            override fun invoke() {
//                func.invoke()
//                if (condition?.invoke(1) == true) return
//                // 创建任务
//                val task = createTask(tag, func, duration, condition)
//                // 因为有延时执行，所以加上延时时间
//                task.uptime += delay
//                // 生成消息内容
//                val message = obtainMessage(task, null)
//                // 定时发送信息
//                handler.sendMessageAtTime(message, task.uptime)
//            }
//
//        }, delay)
    }

    /**
     * 清除指定标签下所有的[timer]、[periodic]任务
     * @param tag 指定标签
     */
    @Synchronized
    fun clearTasks(tag: Any?) {
        for (entry in handleWhats.entries) {
            if (tag === entry.value) {
                handler.removeMessages(entry.key)
                handleWhats.remove(entry.key)
                break
            }
        }
    }

    /**
     * 清除所有的[timer]、[periodic]任务
     */
    @Synchronized
    fun clearTasks() {
        handleWhats.forEach { entry -> handler.removeMessages(entry.key) }
        handleWhats.clear()
    }

    /**
     * 创建任务
     * @param tag       标识
     * @param func      回调
     * @param duration  间隔时间
     * @param condition 终止条件
     */
    @Synchronized
    private fun createTask(
        tag: Any?,
        func: (() -> Unit)?,
        duration: Int?,
        condition: ((Int) -> Boolean)?,
    ): GetHandleTask {
        return GetHandleTask().apply {
            this.tag = tag
            this.func = func
            this.duration = duration ?: 0
            this.uptime = SystemClock.uptimeMillis() + this.duration
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
        // 获取最新What
        val newWhat = what ?: ++currentWhat
        // 生成消息内容
        val message = Message.obtain()
        message.what = newWhat
        message.obj = task
        // 缓存Tag
        handleWhats[newWhat] = task.tag
        Log.e("数据", "${handleWhats.size}")

        return message
    }

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable实例
     */
    @Synchronized
    fun addDisposable(tag: Any?, disposable: Any) = obtain(tag).add(disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    @Synchronized
    fun addDisposables(tag: Any?, vararg disposables: Any) = obtain(tag).addAll(disposables.toMutableList())

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    @Synchronized
    fun addDisposables(tag: Any?, disposables: MutableList<Any>) = obtain(tag).addAll(disposables)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    @Synchronized
    fun clearDisposables(tag: Any?) {
        // 遍历匹配Tag
        for (entry in disposableMap.entries) {
            // 匹配Tag成功
            if (tag === entry.key) {
                // 处理所有Disposable
                entry.value.forEach(::onDispose)
                // 从缓存中清除
                disposableMap.remove(entry.key)
                return
            }
        }
    }

    /**
     * 清除所有Disposable
     */
    @Synchronized
    fun clearDisposables() {
        disposableMap.forEach { entry ->
            val list = entry.value
            list.forEach(::onDispose)
        }
        disposableMap.clear()
    }

    /**
     * 处理单个Disposable
     * @param disposable 待处理实例
     */
    private fun onDispose(disposable: Any?) {
        disposeRx2(disposable)
        disposeRx3(disposable)
    }

    /**
     * 处理Rxjava2的Disposable
     */
    private fun disposeRx2(disposable: Any?) {
        if (rx2Supported) try {
            if (disposable is io.reactivex.disposables.Disposable) {
                disposable.dispose()
            }
        } catch (ignore: NoClassDefFoundError) {
            rx2Supported = false
        }
    }

    /**
     * 处理Rxjava3的Disposable
     */
    private fun disposeRx3(disposable: Any?) {
        if (rx3Supported) try {
            if (disposable is io.reactivex.rxjava3.disposables.Disposable) {
                disposable.dispose()
            }
        } catch (ignore: NoClassDefFoundError) {
            rx3Supported = false
        }
    }
    
    companion object {

        /** 是否支持RxJava2 **/
        private var rx2Supported = true

        /** 是否支持RxJava2 **/
        private var rx3Supported = true

        /** 委托缓存 [GetHandleDelegate] **/
        private val handleDelegates =
            Collections.synchronizedMap(HashMap<GetHandleProvider, GetHandleDelegate>())

        @JvmStatic
        @Synchronized
        fun get(provider: GetHandleProvider): GetHandleDelegate =
            handleDelegates[provider] ?: GetHandleDelegate(provider)
    }
}