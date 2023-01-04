package cn.cqray.android.app

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipDelegate
import cn.cqray.android.tip.GetTipProvider
import java.io.Serializable
import kotlin.collections.HashMap

internal open class GetDelegate<T : GetProvider>(@Suppress("unused") val provider: T): Serializable {

    init {
        if (provider !is ComponentActivity && provider !is Fragment) {
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    onCleared()
                }
            })
        } else {
            throw RuntimeException(
                String.format(
                    "[%s] must extends [%s] or [%s].",
                    provider.javaClass.name,
                    ComponentActivity::class.java.name,
                    Fragment::class.java.name
                )
            )
        }
    }

    @Suppress("unused")
    val lifecycleOwner: LifecycleOwner
        get() = provider as LifecycleOwner

    internal open fun onCleared() = cacheDelegates.remove(provider)

    companion object {

        /** 委托缓存 [StateDelegate] **/
        private val cacheDelegates: HashMap<GetProvider, GetDelegate<out GetProvider>> by lazy(::HashMap)

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
