package cn.cqray.android.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.log.GetLog
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipDelegate
import cn.cqray.android.tip.GetTipProvider
import java.io.Serializable
import java.lang.RuntimeException
import kotlin.collections.HashMap

open class GetDelegate<T>(val provider: T) : Serializable {

    init {
        // 检查Provider是否合法
        GetUtils.checkProvider(provider as Any)
        // 资源回收事件订阅
        (provider as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 从缓存中移除
                val delegate = cacheDelegates.remove(provider)
                delegate?.let { printLog(provider, it, false) }
            }
        })
    }

    @Suppress("unused")
    val lifecycleOwner: LifecycleOwner
        get() = provider as LifecycleOwner

    companion object {

        /** 委托缓存 [StateDelegate] **/
        private val cacheDelegates = HashMap<Any, GetDelegate<Any>>(20)

        @JvmStatic
        @Synchronized
        @Suppress("unchecked_cast")
        fun <T : GetDelegate<*>> get(provider: Any): T {
            var delegate = cacheDelegates[provider]
            delegate = when (provider) {
                is GetTipProvider -> delegate ?: GetTipDelegate(provider) as GetDelegate<Any>
                is StateProvider -> delegate ?: StateDelegate(provider) as GetDelegate<Any>
                else -> null
            }
            if (delegate == null) {
                throw RuntimeException()
            }
            cacheDelegates[provider] = delegate
            printLog(provider, delegate, true)
            return delegate as T
        }

        /**
         * 打印日志
         * @param provider  接口提供者
         * @param delegate  委托实现
         * @param create    是否是创建
         */
        private fun printLog(provider: Any, delegate: GetDelegate<*>, create: Boolean) {
            GetLog.d(
                GetDelegate::class.java,
                String.format(
                    "%s is %s in %s[%d]",
                    delegate.javaClass.simpleName,
                    if (create) "created" else "cleared",
                    provider.javaClass.name,
                    provider.hashCode()
                )
            )
        }
    }
}
