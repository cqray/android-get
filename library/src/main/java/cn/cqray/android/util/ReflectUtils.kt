package cn.cqray.android.util

import java.lang.reflect.ParameterizedType

internal object ReflectUtils {

    @JvmOverloads
    fun getActualTypeArgument(clazz: Class<*>, index: Int = 0): Class<*>? {
        val type = clazz.genericSuperclass
        if (type is ParameterizedType) {
            return type.actualTypeArguments.getOrNull(index) as? Class<*>
        }
        return null
    }
}