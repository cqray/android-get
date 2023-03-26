package cn.cqray.android.handle

/**
 * RxJava相关功能提供器
 * @author Cqray
 */
@Suppress(
    "Deprecation",
    "Unused"
)
@JvmDefaultWithoutCompatibility
interface GetRxProvider {

    /**
     *  获取并初始化[GetHandleProvider]
     */
    val rxDelegate: GetRxDelegate

    /**
     * 添加Disposable
     * @param disposable Disposable
     */
    fun addDisposable(disposable: Any) = rxDelegate.addDisposable(disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    fun addDisposable(tag: Any?, disposable: Any) = rxDelegate.addDisposable(tag, disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    fun addDisposables(vararg disposables: Any) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    fun addDisposables(tag: Any?, vararg disposables: Any) = rxDelegate.addDisposables(tag, disposables)

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    fun addDisposables(disposables: MutableList<Any>) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    fun addDisposables(tag: Any?, disposables: MutableList<Any>) = rxDelegate.addDisposables(tag, disposables)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    fun clearDisposables(tag: Any?) = rxDelegate.clearDisposables(tag)

    /**
     * 清除所有Disposable
     */
    fun clearDisposables() = rxDelegate.clearDisposables()
}