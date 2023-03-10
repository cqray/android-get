package cn.cqray.android.handle

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
    @JvmDefault
    fun addDisposable(disposable: Any) = rxDelegate.addDisposable(disposable)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposable Disposable
     */
    @JvmDefault
    fun addDisposable(tag: Any?, disposable: Any) = rxDelegate.addDisposable(tag, disposable)

    /**
     * 添加Disposable
     * @param disposables Disposable数组
     */
    @JvmDefault
    fun addDisposables(vararg disposables: Any) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable数组
     */
    @JvmDefault
    fun addDisposables(tag: Any?, vararg disposables: Any) = rxDelegate.addDisposables(tag, disposables)

    /**
     * 添加Disposable
     * @param disposables Disposable列表
     */
    @JvmDefault
    fun addDisposables(disposables: MutableList<Any>) = rxDelegate.addDisposables(disposables)

    /**
     * 添加Disposable
     * @param tag 标识
     * @param disposables Disposable列表
     */
    @JvmDefault
    fun addDisposables(tag: Any?, disposables: MutableList<Any>) = rxDelegate.addDisposables(tag, disposables)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    @JvmDefault
    fun clearDisposables(tag: Any?) = rxDelegate.clearDisposables(tag)

    /**
     * 清除所有Disposable
     */
    @JvmDefault
    fun clearDisposables() = rxDelegate.clearDisposables()
}