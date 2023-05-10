package cn.cqray.android.handle

interface GetRxDelegate2 {

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
    fun addDisposable(tag: Any?, disposable: Any)

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
    fun addDisposables(tag: Any?, vararg disposables: Any)

    /**
     * 清除指定Tag下所有Disposable
     * @param tag 指定Tag
     */
    fun clearDisposables(tag: Any?)

    /**
     * 清除所有Disposable
     */
    fun clearAllDisposables()

    fun dispose(tag: Any?)

    fun disposeAll()
}