package cn.cqray.android.handle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.log.GetLog
import kotlin.collections.HashMap

class GetRxDelegate(val provider: GetRxProvider? = null) {

    /** Disposable集合 **/
    private val disposableMap = HashMap<Any?, MutableList<Any>>()

    init {
        if (provider is LifecycleOwner) {
            // 自动销毁
            provider.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    clearDisposables()
                }
            })
        }
    }

    /**
     * 提取对应标识的Disposable列表
     * @param tag 标识
     */
    private fun obtain(tag: Any?): MutableList<Any> {
        // 获取Disposable列表，没有则创建新的
        return disposableMap[tag] ?: mutableListOf<Any>().also { disposableMap[tag] = it }
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
    fun addDisposables(vararg disposables: Any) = addDisposables(null, disposables)

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
                entry.value.forEach(::onDispose)
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
            list.forEach(::onDispose)
        }
        disposableMap.clear()
    }

    /**
     * 处理单个Disposable
     * @param disposable 待处理实例
     */
    private fun onDispose(disposable: Any?) {
        disposeRx2(disposable)
        disposeRx3(disposable)
    }

    /**
     * 处理Rxjava2的Disposable
     */
    private fun disposeRx2(disposable: Any?) {
        if (rx2Supported) try {
            if (disposable is io.reactivex.disposables.Disposable) {
                disposable.dispose()
            }
        } catch (ignore: NoClassDefFoundError) {
            rx2Supported = false
        }
    }

    /**
     * 处理Rxjava3的Disposable
     */
    private fun disposeRx3(disposable: Any?) {
        if (rx3Supported) try {
            if (disposable is io.reactivex.rxjava3.disposables.Disposable) {
                disposable.dispose()
            }
        } catch (ignore: NoClassDefFoundError) {
            rx3Supported = false
        }
    }

    companion object {

        /** 是否支持RxJava2 **/
        private var rx2Supported = true

        /** 是否支持RxJava2 **/
        private var rx3Supported = true

    }
}