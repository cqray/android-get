package cn.cqray.android.app.delegate

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.app.GetManager
import cn.cqray.android.app.provider.GetNavProvider
import cn.cqray.android.app.provider.GetProvider
import cn.cqray.android.app.provider.GetViewProvider
import cn.cqray.android.log.GetLog
import java.util.HashMap

@Suppress("unchecked_cast", "unused")
open class GetDelegate<P : GetProvider>(open val provider: P) {

    private lateinit var providerClassName: String

    init {
        // 订阅资源回收
        val owner = provider.getLifecycleOwner()
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 延时回收资源
                GetManager.runOnUiThreadDelayed({

                    val key = "${provider.hashCode()}-$providerClassName"
                    cacheDelegates[key]?.onCleared()
                    cacheDelegates.remove(key)
                })
            }
        })
    }

    protected open fun onCleared() {}

    companion object {

        /** 委托缓存 [GetDelegate] **/
        private val cacheDelegates = HashMap<String, GetDelegate<*>>()

        @JvmStatic
        @Synchronized
        fun <P : GetProvider, D : GetDelegate<*>> get(provider: P, clazz: Class<P>): D {
            val key = "${provider.hashCode()}-${clazz.name}"
            val delegate = cacheDelegates[key] ?: when (clazz) {
                GetViewProvider::class.java -> GetViewDelegate(provider as GetViewProvider)
                GetNavProvider::class.java -> GetNavDelegate(provider as GetNavProvider)
                else -> throw RuntimeException()
            }
            delegate.providerClassName = clazz.name
            // 存入缓存中
            cacheDelegates[key] = delegate
            return delegate as D
        }
    }
}