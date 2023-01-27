package cn.cqray.android.util

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
}