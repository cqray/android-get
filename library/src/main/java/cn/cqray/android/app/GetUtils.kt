package cn.cqray.android.app

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

internal object GetUtils {
    fun isGetFragmentClass(clazz: Class<*>?): Boolean {
        return (clazz != null && Fragment::class.java.isAssignableFrom(clazz)
                && GetNavProvider::class.java.isAssignableFrom(clazz))
    }

    fun isGetActivityClass(clazz: Class<*>?): Boolean {
        return (clazz != null && ComponentActivity::class.java.isAssignableFrom(clazz)
                && GetNavProvider::class.java.isAssignableFrom(clazz))
    }

    fun checkProvider(provider: Any) {
        if (provider is ComponentActivity || provider is Fragment) {
            return
        }
        throw RuntimeException(
            String.format(
                "%s must extends %s or %s.",
                provider.javaClass.name,
                ComponentActivity::class.java.name,
                Fragment::class.java.name
            )
        )
    }
}