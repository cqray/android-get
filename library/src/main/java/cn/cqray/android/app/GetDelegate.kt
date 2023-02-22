package cn.cqray.android.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.log.GetLog
import cn.cqray.android.log.GetLogLevel
import cn.cqray.android.state.StateAdapter
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.ui.multi.GetMultiDelegate
import cn.cqray.android.ui.multi.GetMultiProvider
import cn.cqray.android.tip.GetTipDelegate
import cn.cqray.android.tip.GetTipProvider
import java.util.HashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * [Get]委托基类
 * @author Cqray
 */
@Suppress("unused")
open class GetDelegate<P : GetProvider>(val provider: P) {

    /** [GetProvider]子类类名（原子对象，实际意义，仅为让变量为 final） **/
    private val providerName = AtomicReference<String>()

    init {
        // 订阅资源回收
        val owner = provider.getLifecycleOwner()
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 延时回收资源
                GetManager.runOnUiThreadDelayed({
                    val key = "${provider.hashCode()}-${providerName.get()}"
                    cacheDelegates[key]?.onCleared()
                    cacheDelegates.remove(key)
                })
            }
        })
    }

    /**
     * 打印日志
     * @param level 日志等级
     * @param method 所在方法
     * @param text 日志内容
     * @param th 异常信息
     */
    internal fun printLog(level: GetLogLevel, method: String, text: String, th: Throwable? = null) {
        // 日志内容
        val message = " \n" +
                "       Owner Class: ${provider::class.java.name}\n" +
                "       Used Method: $method\n" +
                "       Content: $text"
        // 打印日志
        GetLog.print(level, this::class.java, message, th)
    }

    protected open fun onCleared() {}

    companion object {

        /** 委托缓存 [GetDelegate] **/
        private val cacheDelegates = HashMap<String, GetDelegate<*>>()

        @JvmStatic
        @Synchronized
        @Suppress("unchecked_cast")
        fun <P : GetProvider, D : GetDelegate<*>> get(provider: P, clazz: Class<P>): D {
            val key = "${provider.hashCode()}-${clazz.name}"
            val delegate = cacheDelegates[key] ?: when (clazz) {
                GetMultiProvider::class.java -> GetMultiDelegate(provider as GetMultiProvider)
                GetNavProvider::class.java -> GetNavDelegate(provider as GetNavProvider)
                GetTipProvider::class.java -> GetTipDelegate(provider as GetTipProvider)
                GetViewProvider::class.java -> GetViewDelegate(provider as GetViewProvider)
//                StateProvider::class.java -> StateDelegate(provider as StateProvider)
                else -> throw RuntimeException()
            }
            delegate.providerName.set(clazz.name)
            // 存入缓存中
            cacheDelegates[key] = delegate
            return delegate as D
        }
    }
}