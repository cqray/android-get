package cn.cqray.android.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.lang.reflect.Type

object JsonUtils {
    private val gson = Gson()

    @JvmStatic
    fun <T> copyProperies(src: Any?, clazz: Class<T>?): T {
        val element = gson.toJsonTree(src)
        return gson.fromJson(element, clazz)
    }

    /**
     * 深拷贝
     * @param src 来源
     * @param clazz 类型
     */
    @JvmStatic
    fun <T> deepClone(src: T, clazz: Class<T>): T {
        var json: String? = null
        runCatching { json = gson.toJson(src) }
        return gson.fromJson(json, clazz)
    }
}