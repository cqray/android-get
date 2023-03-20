package cn.cqray.android.util

import java.lang.reflect.ParameterizedType

object ReflectUtil {

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

    fun getActualTypeArgument(clazz: Class<*>): Class<*>? {
        val type = clazz.genericSuperclass
        if (type is ParameterizedType) {
            return type.actualTypeArguments?.getOrNull(0) as Class<*>
        }
        return null
    }
}