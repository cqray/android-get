package cn.cqray.android.lifecycle

import android.app.Application
import android.util.Log
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
class GetViewModelFactory(owner: ViewModelStoreOwner) : NewInstanceFactory() {
    private val lifecycleOwner: LifecycleOwner
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            when {
                GetViewModel::class.java.isAssignableFrom(modelClass) -> {
                    Log.e("数据", "66666666|${lifecycleOwner.lifecycle.currentState.name}")
                    val constructor = modelClass.getConstructor(LifecycleOwner::class.java)


                    val cs = modelClass.constructors
                    for (c in cs) {
                        Log.e("数据", "66666667${c.toString()}")
                    }
                    Log.e("数据", "66666667")
                    val ree = constructor.newInstance(lifecycleOwner)
                    Log.e("数据", "66666668")
                    ree
                    //modelClass.getConstructor(LifecycleOwner::class.java).newInstance(lifecycleOwner)
                }
                AndroidViewModel::class.java.isAssignableFrom(modelClass) -> {
                    Log.e("数据", "777777")
                    modelClass.getConstructor(Application::class.java).newInstance(Get.application)
                }
                else -> modelClass.getConstructor().newInstance()
            }
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of \$modelClass", e)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
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