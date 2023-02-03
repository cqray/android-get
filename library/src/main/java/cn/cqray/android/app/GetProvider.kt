package cn.cqray.android.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import cn.cqray.android.lifecycle.GetViewModelProvider

@JvmDefaultWithoutCompatibility
interface GetProvider {

//    val viewModelProvider: GetViewModelProvider by lazy { GetViewModelProvider(getLifecycleOwner()) }

    fun getLifecycleOwner(): LifecycleOwner {
        if (this is LifecycleOwner) {
            return this
        }
        throw RuntimeException("[GetProvider] must be implemented on LifecycleOwner.")
    }

    @Suppress("Unchecked_cast")
    fun <T : ViewModel>getViewModel(clazz: Class<out ViewModel>): T {
        val owner = getLifecycleOwner()
        if (owner is ViewModelStoreOwner) {
            return GetViewModelProvider(owner).get(clazz) as T
        }
        throw java.lang.RuntimeException()
    }
}
