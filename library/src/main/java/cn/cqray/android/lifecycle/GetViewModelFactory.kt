package cn.cqray.android.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.reflect.InvocationTargetException

/**
 * [GetViewModel]工厂
 * @author Cqray
 */
class GetViewModelFactory(owner: ViewModelStoreOwner?) : NewInstanceFactory() {
    private var lifecycleOwner: LifecycleOwner? = null
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (GetViewModel::class.java.isAssignableFrom(modelClass)) {
            try {
                modelClass.getConstructor(LifecycleOwner::class.java).newInstance(lifecycleOwner)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException("Cannot create an instance of \$modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of \$modelClass", e)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of \$modelClass", e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException("Cannot create an instance of \$modelClass", e)
            }
        } else super.create(modelClass)
    }

    init {
        lifecycleOwner = if (owner is LifecycleOwner) {
            owner
        } else {
            throw IllegalArgumentException("The owner must implements LifecycleOwner.")
        }
    }
}