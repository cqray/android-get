package cn.cqray.android.lifecycle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.lifecycle.ViewModelStoreOwner
import cn.cqray.android.Get
import java.lang.reflect.InvocationTargetException

/**
 * [GetViewModel]工厂
 * @author Cqray
 */
class GetViewModelFactory(owner: ViewModelStoreOwner?) : NewInstanceFactory() {
    private var lifecycleOwner: LifecycleOwner? = null
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            when {
                GetViewModel::class.java.isAssignableFrom(modelClass) -> {
                    modelClass.getConstructor(LifecycleOwner::class.java).newInstance(lifecycleOwner)
                }
                AndroidViewModel::class.java.isAssignableFrom(modelClass) -> {
                    modelClass.getConstructor(Application::class.java).newInstance(Get.application)
                }
                else -> super.create(modelClass)
            }
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        }
    }

    init {
        lifecycleOwner = if (owner is LifecycleOwner) {
            owner
        } else {
            throw IllegalArgumentException("The owner must implements LifecycleOwner.")
        }
    }
}