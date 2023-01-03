package cn.cqray.android.app2

import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import cn.cqray.android.Get
import java.util.*
import kotlin.collections.HashMap

/**
 * [Get]框架Result请求管理器
 * @author Cqray
 */
object GetResultManager {

    /** 接收者列表 **/
    private val receiverList = LinkedList<LifecycleOwner>()

    /** 数据管理Map **/
    private val dataMap = HashMap<LifecycleOwner, MutableLiveData<Bundle>>()

    /** 取消注册观察者 **/
    private val unregisterObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            receiverList.remove(owner)
        }
    }

    /**
     * 注册接收者，并添加到栈顶
     * @param receiver 接收器 [LifecycleOwner]
     * @param callback 接收结果回调 [GetIntentCallback]
     */
    @Synchronized
    fun registerReceiver(receiver: LifecycleOwner?, callback: GetIntentCallback?) {
        if (receiver == null) return
        // 设置顶级接收者
        setTopReceiver(receiver)
        // 获取对应的MutableLiveData
        var data = dataMap[receiver]
        if (data == null) {
            data = MutableLiveData<Bundle>()
            dataMap[receiver] = data
        }
        // 移除所有订阅
        data.removeObservers(receiver)
        // 重新订阅
        data.observe(receiver) { bundle -> callback?.onResult(bundle) }
    }

    /**
     * 向栈顶接收者发送数据
     * @param data 数据 [Bundle]
     */
    @Synchronized
    fun sendToTopReceiver(data: Bundle?) {
        if (data == null || receiverList.isEmpty()) return
        val receiver = receiverList.first
        dataMap[receiver]?.value = data
    }

    /**
     * 设置栈顶接收者
     * @param receiver 接收者 [LifecycleOwner]
     */
    private fun setTopReceiver(receiver: LifecycleOwner) {
        if (receiverList.contains(receiver)) {
            if (receiverList.first != receiver) {
                receiverList.remove(receiver)
                receiverList.addFirst(receiver)
            }
        } else {
            receiverList.addFirst(receiver)
        }
        // 重新订阅
        receiver.lifecycle.removeObserver(unregisterObserver)
        receiver.lifecycle.addObserver(unregisterObserver)
    }
}