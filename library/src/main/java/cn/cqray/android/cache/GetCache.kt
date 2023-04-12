package cn.cqray.android.cache

import com.blankj.utilcode.util.SPUtils
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

    /** [SPUtils]实例 **/
    private val spUtils = SPUtils.getInstance(this.javaClass.name)

    /**[Gson]Json格式化实例 **/
    private val gson = Gson()

    /**
     * 同步提交对象
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: Any?) {
        if (value == null) spUtils.remove(key, true)
        else spUtils.put(key, gson.toJson(value), true)
    }

    /**
     * 异步提交对象
     * @param key   键
     * @param value 值
     */
    fun putAsync(key: String, value: Any?) {
        if (value == null) spUtils.remove(key, false)
        else spUtils.put(key, gson.toJson(value), false)
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
        runCatching {
            val json = spUtils.getString(key, null)
            return gson.fromJson(json, clazz)
        }
        return null
    }

    fun <T> getObject(key: String, type: Type): T? {
        runCatching {
            val json = spUtils.getString(key, null)
            return gson.fromJson(json, type)
        }
        return null
    }
}