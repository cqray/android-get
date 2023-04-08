package cn.cqray.android.app

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.UnsupportedOperationException

@Suppress(
    "Deprecation",
    "Unchecked_cast"
)
@JvmDefaultWithoutCompatibility
interface GetProvider {

    fun getLifecycleOwner(): LifecycleOwner {
        if (this is LifecycleOwner) {
            return this
        }
        throw RuntimeException("[GetProvider] can't get LifecycleOwner.")
    }

    fun <T : ViewModel> getViewModel(clazz: Class<out ViewModel>): T {
        val owner = getLifecycleOwner()
        if (owner is ViewModelStoreOwner) {
            return ViewModelProvider(owner).get(clazz) as T
        }
        throw UnsupportedOperationException("${owner.javaClass.simpleName} can't supported ${ViewModelProvider::class.java.simpleName}")
    }
}
