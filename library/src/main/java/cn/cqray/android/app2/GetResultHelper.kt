package cn.cqray.android.app2

import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.lifecycle.GetLiveData
import java.util.*

/**
 * [Get]框架Result请求管理器
 * @author Cqray
 */
internal object GetResultHelper {

    /** 接收者列表 **/
    private val receiverStack = Stack<LifecycleOwner>()

    /** 数据[GetLiveData]集合 **/
    private val dataLds = Collections.synchronizedMap<LifecycleOwner, GetLiveData<Bundle>>(mutableMapOf())

    /** 取消注册观察者 **/
    private val unregisterObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            // 数据清除
            dataLds.remove(owner)
            receiverStack.remove(owner)
        }
    }

    /**
     * 注册接收者，并添加到栈顶
     * @param receiver 接收器
     * @param callback 接收结果回调
     */
    @Synchronized
    fun registerReceiver(receiver: LifecycleOwner, callback: Function1<Bundle, Unit>) {
        // 设置顶级接收者
        setTopReceiver(receiver)
        // 获取对应的MutableLiveData
        val data = dataLds[receiver] ?: GetLiveData<Bundle>().also { dataLds[receiver] = it }
        // 移除所有订阅
        data.removeObservers(receiver)
        // 重新订阅
        data.observe(receiver) { bundle -> callback.invoke(bundle) }
    }

    /**
     * 向栈顶接收者发送数据
     * @param data 数据 [Bundle]
     */
    @Synchronized
    fun sendToTopReceiver(data: Bundle) {
        // 找到栈顶的接收器
        receiverStack.lastOrNull()?.let {
            // 发送数据
            dataLds[it]?.setValue(data)
        }
    }

    /**
     * 设置栈顶接收者
     * @param receiver 接收者 [LifecycleOwner]
     */
    private fun setTopReceiver(receiver: LifecycleOwner) {
        if (receiverStack.contains(receiver)) {
            // 重新加入接收栈，并放置于栈顶
            receiverStack.remove(receiver)
            receiverStack.add(receiver)

        } else receiverStack.add(receiver)
        // 重新订阅，确保只有一个观察者
        receiver.lifecycle.removeObserver(unregisterObserver)
        receiver.lifecycle.addObserver(unregisterObserver)
    }
}