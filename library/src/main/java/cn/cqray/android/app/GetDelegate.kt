package cn.cqray.android.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipDelegate
import cn.cqray.android.tip.GetTipProvider
import java.io.Serializable
import kotlin.collections.HashMap

interface GetProvider : Serializable

open class GetDelegate<T : GetProvider>(@Suppress("unused") val provider: T) : Serializable {

    init {
        // 检查Provider是否合法
        GetUtils.checkProvider(provider)
        // 资源回收事件订阅
        (provider as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 从缓存中移除
                cacheDelegates.remove(provider)
            }
        })
    }

    @Suppress("unused")
    val lifecycleOwner: LifecycleOwner
        get() = provider as LifecycleOwner

    companion object {

        /** 委托缓存 [StateDelegate] **/
        private val cacheDelegates by lazy { HashMap<GetProvider, out GetDelegate<out GetProvider>>(20) }

        @JvmStatic
        @Synchronized
        @Suppress("unchecked_cast")
        fun <T : GetDelegate<*>> get(provider: GetProvider?): T {
            val delegate = cacheDelegates[provider]
            when (provider) {
                is GetTipProvider -> delegate ?: GetTipDelegate(provider)
                is StateProvider -> delegate ?: StateDelegate(provider)
                else -> throw RuntimeException()
            }
            cacheDelegates[provider] = delegate!!
            return delegate as T
        }
    }
}
