package cn.cqray.android.handle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.collections.HashMap

/**
 * RxJava委托实现
 * @author Cqray
 */
@Suppress("MemberVisibilityCanBePrivate")
class GetRxDelegate @JvmOverloads constructor(
    /** RxJava Disposable相关方法提供器 **/
    owner: LifecycleOwner? = null
) {

    /** Disposable集合 **/
    private val disposableMap = HashMap<Any?, MutableList<Disposable>>()

    init {
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
    private fun obtain(tag: Any?): MutableList<Disposable> {
        // 获取Disposable列表，没有则创建新的
        return disposableMap[tag] ?: mutableListOf<Disposable>().also { disposableMap[tag] = it }
    }

    /**
     * 添加Disposable
     * @param disposable Disposable
     */
    fun addDisposable(disposable: Disposable) = addDisposable(null, disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    @Synchronized
    fun addDisposable(tag: Any?, disposable: Disposable) = obtain(tag).add(disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    fun addDisposables(vararg disposables: Disposable) = addDisposables(null, *disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    @Synchronized
    fun addDisposables(tag: Any?, vararg disposables: Disposable) = obtain(tag).addAll(disposables.toMutableList())

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    fun addDisposables(disposables: MutableList<Disposable>) = addDisposables(null, disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    @Synchronized
    fun addDisposables(tag: Any?, disposables: MutableList<Disposable>) = obtain(tag).addAll(disposables)

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
        for (entry in disposableMap.entries) {
            // 匹配Tag成功
            if (tag === entry.key) {
                // 处理所有Disposable
                entry.value.forEach { d -> d.dispose() }
                // 从缓存中清除
                disposableMap.remove(entry.key)
                return
            }
        }
    }

    /**
     * 清除所有Disposable
     */
    @Synchronized
    fun clearDisposables() {
        disposableMap.forEach { entry ->
            val list = entry.value
            list.forEach { d -> d.dispose() }
        }
        disposableMap.clear()
    }

}