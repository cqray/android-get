package cn.cqray.android.app

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import cn.cqray.android.lifecycle.GetViewModelProvider
import java.lang.UnsupportedOperationException

@Suppress(
    "Deprecation",
    "Unchecked_cast"
)
@JvmDefaultWithoutCompatibility
interface GetProvider {

    @JvmDefault
    fun getLifecycleOwner(): LifecycleOwner {
        if (this is LifecycleOwner) {
            return this
        }
        throw RuntimeException("[GetProvider] must be implemented on LifecycleOwner.")
    }

    @JvmDefault
    fun <T : ViewModel> getViewModel(clazz: Class<out ViewModel>): T {
        val owner = getLifecycleOwner()
        if (owner is ViewModelStoreOwner) {
            return GetViewModelProvider(owner).get(clazz) as T
        }
        throw UnsupportedOperationException("${owner.javaClass.simpleName} can't supported ${GetViewModelProvider::class.java.simpleName}")
    }
}
