package cn.cqray.android.util

import java.lang.reflect.ParameterizedType

object ReflectUtils {

    fun setField(src: Any, name: String, value: Any?) {
        val cls = src.javaClass
        kotlin.runCatching {
            val field = cls.getField(name)
            field.isAccessible = true
            field.set(src, value)
        }.onFailure {
            kotlin.runCatching {
                val field = cls.getDeclaredField(name)
                field.isAccessible = true
                field.set(src, value)
            }
        }
    }

    @JvmOverloads
    fun getActualTypeArgument(clazz: Class<*>, index: Int = 0): Class<*>? {
        val type = clazz.genericSuperclass
        if (type is ParameterizedType) {
            return type.actualTypeArguments.getOrNull(index) as? Class<*>
        }
        return null
    }
}