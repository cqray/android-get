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

    @JvmStatic
    fun <T> deepClone(src: T?, clazz: Class<T>?): T? {
        if (clazz == null) return null
        var element :String?= null
        try {

             element = gson.toJson(src)
        } catch (e : Exception) {
            e.printStackTrace()
        }
        //val element = gson.toJsonTree(src)
        return gson.fromJson(element, clazz)
    }
}