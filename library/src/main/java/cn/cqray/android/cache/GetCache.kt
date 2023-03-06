package cn.cqray.android.cache

import android.content.Context
import android.content.SharedPreferences
import cn.cqray.android.Get
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * 缓存基础实现
 * @author Cqray
 */
@Suppress(
    "ApplySharedPref",
    "MemberVisibilityCanBePrivate",
    "Unused"
)
open class GetCache {

    /** [SharedPreferences]实例 **/
    private val sp: SharedPreferences = Get.context.getSharedPreferences(
        this.javaClass.name,
        Context.MODE_PRIVATE
    )

    /**[Gson]Json格式化实例 **/
    private val gson = Gson()

    /**
     * 同步提交对象
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Any?) {
        if (value == null) sp.edit().remove(key).commit()
        else sp.edit().putString(key, gson.toJson(value)).commit()
    }

    /**
     * 异步提交对象
     * @param key   键
     * @param value 值
     */
    fun putAsync(key: String, value: Any?) {
        if (value == null) sp.edit().remove(key).apply()
        else sp.edit().putString(key, gson.toJson(value)).apply()
    }

    fun getBoolean(key: String) = getBoolean(key, false)

    fun getBoolean(key: String, def: Boolean) = getObject(key, Boolean::class.java) ?: def

    fun getInt(key: String) = getInt(key, 0)

    fun getInt(key: String, def: Int) = getObject(key, Int::class.java) ?: def

    fun getFloat(key: String) = getFloat(key, 0F)

    fun getFloat(key: String, def: Float) = getObject(key, Float::class.java) ?: def

    fun getLong(key: String) = getLong(key, 0L)

    fun getLong(key: String, def: Long) = getObject(key, Long::class.java) ?: def

    fun getString(key: String) = getString(key, null)

    fun getString(key: String, def: String?) = getObject(key, String::class.java) ?: def

    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val value = sp.getString(key, null)
        return gson.fromJson(value, clazz)
    }

    fun <T> getObject(key: String, type: Type): T? {
        val value = sp.getString(key, null)
        return gson.fromJson(value, type)
    }
}