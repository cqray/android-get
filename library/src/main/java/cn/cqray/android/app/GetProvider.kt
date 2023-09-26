package cn.cqray.android.app

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.UnsupportedOperationException

@Suppress("Unchecked_cast")
@JvmDefaultWithoutCompatibility
interface GetProvider {

    /// 生命周期拥有者
    val lifecycleOwner: LifecycleOwner
        get() {
            if (this is LifecycleOwner) return this
            throw UnsupportedOperationException("${this.javaClass.simpleName} doesn't implement LifecycleOwner.")
        }

    /// 获取当前实例的ViewModel
    fun <T : ViewModel> getViewModel(clazz: Class<out ViewModel>): T {
        val owner = lifecycleOwner
        if (owner is ViewModelStoreOwner) {
            return ViewModelProvider(owner).get(clazz) as T
        }
        throw UnsupportedOperationException("${owner.javaClass.simpleName} doesn't implement ViewModelStoreOwner.")
    }

    /// 获取父级实例的ViewModel
    fun <T : ViewModel> getParentViewModel(clazz: Class<out ViewModel>): T {
        val owner = lifecycleOwner
        if (owner is Fragment) return ViewModelProvider(owner.requireActivity()).get(clazz) as T
        return getViewModel(clazz)
    }
}
