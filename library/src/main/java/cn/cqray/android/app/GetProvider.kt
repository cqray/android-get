package cn.cqray.android.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

@JvmDefaultWithoutCompatibility
interface GetProvider {

//    val lifecycleOwner: LifecycleOwner
//        get() = {
//            if (this is LifecycleOwner) this as LifecycleOwner
//            else throw RuntimeException()
//        }

    fun getLifecycleOwner(): LifecycleOwner {
        if (this is LifecycleOwner) {
            return this
        }
        throw RuntimeException()
    }
}
