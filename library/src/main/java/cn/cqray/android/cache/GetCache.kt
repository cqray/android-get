package cn.cqray.android.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import cn.cqray.android.Get

open class GetCache(mode: Int? = null) {

    private var sp: SharedPreferences = Get.context.getSharedPreferences(
        this.javaClass.name,
        mode ?: Context.MODE_PRIVATE
    )

    /**
     * 同步提交
     * @param key   键
     * @param value 值
     */
    @SuppressLint("ApplySharedPref")
    fun put(key: String, value: String?) = sp.edit().putString(key, value).commit()

    /**
     * 异步提交
     * @param key   键
     * @param value 值
     */
    fun putAsync(key: String, value: String?) = sp.edit().putString(key, value).apply()
}