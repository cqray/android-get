package cn.cqray.android.handle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*
import kotlin.collections.HashMap

/**
 * RxJava委托实现
 * @author Cqray
 */
@Suppress("MemberVisibilityCanBePrivate")
class GetRxDelegate3 @JvmOverloads constructor(
    /** RxJava Disposable相关方法提供器 **/
    owner: LifecycleOwner? = null
) {
    /** Disposable集合 **/
    private val disposables = Collections.synchronizedMap(HashMap<Any?, MutableList<Any>>())

    init {
        // 检查是否支持
        checkSupported()
        // 自定管理生命周期
        owner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                clearDisposables()
            }
        })
    }

    /**
     * 提取对应标识的Disposable列表
     * @param tag 标识
     */
    private fun obtain(tag: Any?): MutableList<Any> {
        // 获取Disposable列表，没有则创建新的
        return disposables[tag] ?: Collections.synchronizedList(
            mutableListOf<Any>()
        ).also { disposables[tag] = it }
    }

    /**
     * 添加Disposable
     * @param disposable Disposable
     */
    fun addDisposable(disposable: Any) = addDisposable(null, disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    @Synchronized
    fun addDisposable(tag: Any?, disposable: Any) = obtain(tag).add(disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    fun addDisposables(vararg disposables: Any) = addDisposables(null, *disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    @Synchronized
    fun addDisposables(tag: Any?, vararg disposables: Any) = obtain(tag).addAll(disposables.toMutableList())

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    fun addDisposables(disposables: MutableList<Any>) = addDisposables(null, disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    @Synchronized
    fun addDisposables(tag: Any?, disposables: MutableList<Any>) = obtain(tag).addAll(disposables)

    /**
     * 根据标识获取对应的列表
     * @param tag 标识
     */
    @Synchronized
    fun getDisposables(tag: Any? = null) = obtain(tag)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    @Synchronized
    fun clearDisposables(tag: Any?) {
        // 遍历匹配Tag
        for (entry in disposables.entries) {
            // 匹配Tag成功
            if (tag === entry.key) {
                // 处理所有Disposable
                entry.value.forEach { d -> dispose(d) }
                // 从缓存中清除
                disposables.remove(entry.key)
                return
            }
        }
    }

    /**
     * 清除所有Disposable
     */
    @Synchronized
    fun clearDisposables() {
        disposables.forEach { entry ->
            val list = entry.value
            list.forEach { d -> dispose(d) }
        }
        disposables.clear()
    }


    /**
     * 释放RxJava任务实例
     * @param disposable 任务实例
     */
    private fun dispose(disposable: Any) {
        // 释放RxJava2
        if (rxJava2Support) if (disposable is io.reactivex.disposables.Disposable) disposable.dispose()
        // 释放RxJava3
        if (rxJava3Support) if (disposable is io.reactivex.rxjava3.disposables.Disposable) disposable.dispose()
    }

    private companion object {

        /** 是否支持RxJava2 **/
        var rxJava2Support: Boolean = true

        /** 是否支持RxJava3 **/
        var rxJava3Support: Boolean = true

        /** 检查是否支持 **/
        fun checkSupported() {
            // 检查RxJava3
            if (rxJava2Support) runCatching {
                io.reactivex.disposables.Disposable::class.java
            }.onFailure { rxJava2Support = false }
            // 检查RxJava3
            if (rxJava3Support) runCatching {
                io.reactivex.rxjava3.disposables.Disposable::class.java
            }.onFailure { rxJava3Support = false }
        }
    }
}