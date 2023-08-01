package cn.cqray.android.app2

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.Serializable
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 * 基础初始配置，主要是可以将配置保存到本地
 * @author Cqray
 */
open class GetBaseInit : Serializable {

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
    internal fun loadFromLocal() = synchronized(GetBaseInit::class.java) {
        if (isNeedLoad) {
            val json = SPUtils.getInstance(name).getString(key)
            val element = JsonParser.parseString(json)
            if (element.isJsonObject) {
                // 读取缓存并复制
                val body = element.asJsonObject
                val gson = Gson()
                // 遍历属性
                javaClass.kotlin.declaredMemberProperties.forEach {
                    runCatching {
                        if (it.javaClass.getAnnotation(Transient::class.java) != null) {
                            // 不执行的类型
                            return@runCatching
                        }
                        val type = it.returnType
                        val value = gson.fromJson(body.get(it.name), it.javaClass)
                        // 字段赋值
                        it.javaField?.let {
                            it.isAccessible = true
                            // 值不为NULL，直接赋值
                            if (value != null) it.set(this, value)
                            // 为NULL则需要判定是否可以为NULL才赋值
                            else if (type.isMarkedNullable) it.set(this, null)
                        }
                    }
                }
            }
            // 标记为不需要重新加载
            isNeedLoad = false
        }
    }

    /** 清空本地缓存 **/
    fun clearLocal() = SPUtils.getInstance(name).remove(key)

    /** 保存到本地 **/
    fun saveToLocal() = synchronized(GetBaseInit::class.java) {
        // 标记为需要重新加载
        isNeedLoad = true
        // 缓存到本地
        SPUtils.getInstance(name).put(key, GsonUtils.toJson(this))
    }
}