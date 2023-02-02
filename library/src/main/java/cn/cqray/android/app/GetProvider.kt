package cn.cqray.android.app

import androidx.lifecycle.LifecycleOwner

@JvmDefaultWithoutCompatibility
interface GetProvider {

    fun getLifecycleOwner(): LifecycleOwner {
        if (this is LifecycleOwner) {
            return this
        }
        throw RuntimeException("[GetProvider] must be implemented on LifecycleOwner.")
    }
}
