package cn.cqray.android.ui.page

import cn.cqray.android.`object`.ResponseData

/**
 * 分页功能提供器
 * @author Cqray
 */
@Suppress(
    "Deprecation",
    "Unused"
)
@JvmDefaultWithoutCompatibility
interface PaginationProvider<T> {

    /** 分页委托 **/
    val paginationDelegate: PaginationDelegate<T>

    /** 设置默认分页起始页码 **/
    @JvmDefault
    fun setDefaultPageNum(pageNum: Int) = run { paginationDelegate.defaultPageNum = pageNum }

    /** 设置默认分页大小 **/
    @JvmDefault
    fun setDefaultPageSize(pageSize: Int) = run { paginationDelegate.defaultPageSize = pageSize }

    /** 设置每页是否必须充满 **/
    @JvmDefault
    fun setPaginationFull(full: Boolean) = run { paginationDelegate.paginationFull = full }

    /** 设置空布局文本 **/
    @JvmDefault
    fun setEmptyText(text: String) = run { paginationDelegate.emptyText = text }

    /** 设置刷新视图是否能够拖拽 **/
    @JvmDefault
    fun setEnableDrag(enable: Boolean) = paginationDelegate.setEnableDrag(enable)

    /**
     * 结束更新，并传入数据
     * @param data 数据
     */
    @JvmDefault
    fun finish(data: List<T>?) = paginationDelegate.finish(data)

    /**
     * 结束更新，并传入数据
     * @param data 数据
     */
    @JvmDefault
    fun finishWithResponse(data: ResponseData<List<T>?>?) = paginationDelegate.finishWithResponse(data)

    /**
     * 结束更新，并传入异常信息
     * @param throwable 异常信息
     */
    @JvmDefault
    fun finishWithException(throwable: Throwable?) = paginationDelegate.finishWithException(throwable)

    /** 自动刷新数据 **/
    @JvmDefault
    fun refreshAutomatic() = paginationDelegate.refreshAutomatic()

    /** 静默刷新数据 **/
    @JvmDefault
    fun refreshSilent() = paginationDelegate.refreshSilent()
}