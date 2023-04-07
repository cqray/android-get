package cn.cqray.android.init

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.Serializable
import java.lang.reflect.Field

/**
 * 基础初始配置，主要是可以将配置保存到本地
 * @author Cqray
 */
open class BaseInit : Serializable {

    /** 是否需要加载本地数据 **/
    @Transient
    private var isNeedLoad = true

    /** 缓存名 **/
    private val name get() = "GetInit_SharedPreferences"

    /** 键值 **/
    private val key get() = javaClass.name

    /**
     * 从本地缓存中加载数据
     */
    internal fun loadFromLocal() = synchronized(BaseInit::class.java) {
        if (isNeedLoad) {
            val json = SPUtils.getInstance(name).getString(key)
            val element = JsonParser.parseString(json)
            if (element.isJsonObject) {
                // 读取缓存并复制
                val body = element.asJsonObject
                val gson = Gson()
                // 获取所有属性
                val fields = mutableListOf<Field>().also {
                    it.addAll(javaClass.fields)
                    it.addAll(javaClass.declaredFields)
                }

                // 遍历所有属性
                fields.forEach {
                    runCatching {
                        if (it.getAnnotation(Transient::class.java) != null) {
                            // 不执行的类型
                            return@runCatching
                        }
                        val value = gson.fromJson(body.get(it.name), it.type)
                        it.isAccessible = true
                        it.set(this, value)
                    }.onFailure { it.printStackTrace() }
                }
            }
            // 标记为不需要重新加载
            isNeedLoad = false
        }
    }

    /** 清空本地缓存 **/
    fun clearLocal() = SPUtils.getInstance(name).remove(key)

    /** 保存到本地 **/
    fun saveToLocal() = synchronized(BaseInit::class.java) {
        // 标记为需要重新加载
        isNeedLoad = true
        // 缓存到本地
        SPUtils.getInstance(name).put(key, GsonUtils.toJson(this))
    }
}