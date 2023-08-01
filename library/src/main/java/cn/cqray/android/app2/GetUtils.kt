package cn.cqray.android.app2

import android.app.Activity
import androidx.fragment.app.Fragment

@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
internal object GetUtils {

    /**
     * 是否是实现了[GetNavProvider]
     * @param clazz 类型
     */
    fun isGetNavProviderClass(clazz: Class<*>?) = clazz != null && GetNavProvider::class.java.isAssignableFrom(clazz)

    /**
     * 是否是[Fragment]
     * @param clazz 类型
     */
    fun isFragmentClass(clazz: Class<*>?) = clazz != null && Fragment::class.java.isAssignableFrom(clazz)

    /**
     * 是否是[Activity]
     * @param clazz 类型
     */
    fun isActivityClass(clazz: Class<*>?) = clazz != null && Activity::class.java.isAssignableFrom(clazz)

    /**
     * 是否是实现了[GetNavProvider]接口的Fragment
     * @param clazz 类型
     */
    fun isGetFragmentClass(clazz: Class<*>?) = isGetNavProviderClass(clazz) && isFragmentClass(clazz)

    /**
     * 是否是实现了[GetNavProvider]接口的Activity
     * @param clazz 类型
     */
    fun isGetActivityClass(clazz: Class<*>?) = isGetNavProviderClass(clazz) && isActivityClass(clazz)
}