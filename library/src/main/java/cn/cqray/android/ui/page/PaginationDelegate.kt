package cn.cqray.android.ui.page

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.`object`.ResponseData
import cn.cqray.android.app.GetViewProvider
import cn.cqray.android.lifecycle.GetLiveData
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTip
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * 分页委托
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class PaginationDelegate<T>(val owner: LifecycleOwner) : StateProvider {

    /** 起始页码，默认为1 **/
    var defaultPageNum = 1

    /** 分页大小，默认为20  */
    var defaultPageSize = 20

    /** 上次加载的数据对应页码  */
    private var lastPageNum = 0

    /** 当前加载的数据对应页码  */
    private var currentPageNum = 0

    /**
     * 分页时是否充满每页。
     * 设置true，表示一页未满指定数量，则没有更多数据。
     * 设置false，表示只有遇到数据为空，才会设置为没有更多数据。
     */
    var paginationFull = true

    /** 空布局文本 **/
    var emptyText: String = ""

    /** 列表适配器  */
    var adapter: BaseQuickAdapter<T, out BaseViewHolder>? = null

    /** 是否是首次刷新数据  */
    private var firstRefresh = true

    /** 刷新视图 **/
    private var refreshLayout: SmartRefreshLayout? = null

    /** 状态委托 **/
    override val stateDelegate: StateDelegate by lazy {
        if (owner is GetViewProvider) owner.stateDelegate
        else StateDelegate()
    }

    /** 主要是为了不让数据在界面不可见时加载，造成APP卡顿  */
    private val data = GetLiveData<List<T>?>()

    /** 是否可以分页  */
    private val paginationEnable = GetLiveData<Boolean>()

    /** 页面刷新回调 **/
    private val callbacks = mutableListOf<Function2<Int, Int, Unit>>()

    /**
     * 设置是否开启分页功能。
     * 设置true，表示可以下拉加载更多。
     * 设置false，表示不可以下拉加载更多。
     */
    var isPaginationEnable: Boolean
        get() = if (paginationEnable.value == null) true else paginationEnable.value!!
        set(enable) = paginationEnable.setValue(enable)

    init {
        initPageInfo(owner)
        initDataObserver(owner)
    }

    /**
     * 初始化分页数据
     */
    private fun initPageInfo(owner: LifecycleOwner) {
        val init = Get.init.paginationInit!!
        defaultPageNum = init.pageNum
        lastPageNum = defaultPageNum
        currentPageNum = defaultPageNum
        defaultPageSize = init.pageSize
        // 分页设置
        paginationEnable.observe(owner) { refreshLayout?.setEnableLoadMore(it) }
    }

    /**
     * 初始化数据变化观察者
     */
    private fun initDataObserver(owner: LifecycleOwner) {
        data.observe(owner) {
            // 结束数据加载状态
            refreshLayout?.let { layout ->
                // 结束控件刷新
                if (layout.isRefreshing) layout.finishRefresh()
                // 结束控件加载更多
                if (layout.isLoading) layout.finishLoadMore()
            }
            // 职位空闲状态
            if (firstRefresh) {
                stateDelegate.setIdle()
                firstRefresh = false
            }
            // 数据是否为空
            val empty = it?.isEmpty() ?: true
            if (currentPageNum == defaultPageNum) {
                // 如果是起始页，数据为空则显示空界面
                if (empty) stateDelegate.setEmpty(emptyText)
                // 显示界面
                else stateDelegate.setIdle()
            }
            // 不需要分页
            if (!isPaginationEnable) {
                // 结束刷新数据
                adapter?.setList(it)
                // 重复设置上拉刷新状态，避免中途有修改
                refreshLayout?.setEnableLoadMore(false)
                return@observe
            }
            // 上次数据页面和本次不能衔接到一起，则做无效处理
            if (currentPageNum - lastPageNum > 1) return@observe
            // 是否有更多数据
            refreshLayout?.setNoMoreData(empty || (paginationFull && it!!.size < defaultPageSize))
            // 如果是第一页
            if (currentPageNum == defaultPageNum) {
                adapter?.setList(it)
                Log.e("数据", "设置并更新了数据")
            } else if (!empty) {
                adapter?.addData(it!!)
            }
            // 记录页码
            lastPageNum = currentPageNum
        }
    }

    /**
     * 添加回调
     * @param callback 回调
     */
    fun addCallback(callback: Function2<Int, Int, Unit>) = callbacks.add(callback)

    /**
     * 重置参数
     */
    fun reset() {
        currentPageNum = defaultPageNum
        firstRefresh = true
    }

    /**
     * 设置刷新视图
     * @param layout 刷新视图
     */
    fun setRefreshLayout(layout: SmartRefreshLayout?) {
        layout?.let {
            refreshLayout = it
            it.setEnablePureScrollMode(false)
            it.setEnableRefresh(true)
            it.setEnableLoadMore(true)
            it.setEnableOverScrollDrag(true)
            it.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    currentPageNum++
                    callbacks.forEach { callback -> callback.invoke(currentPageNum, defaultPageSize) }
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    currentPageNum = defaultPageNum
                    callbacks.forEach { callback -> callback.invoke(currentPageNum, defaultPageSize) }
                }
            })
        }
        stateDelegate.attachLayout(refreshLayout)
    }

    /**
     * 设置纯滚动模式
     */
    fun setEnablePureScrollMode(enable: Boolean) = refreshLayout?.setEnablePureScrollMode(enable)

    /**
     * 结束更新，并传入数据
     * @param data 数据
     */
    fun finish(data: List<T>?) = this.data.setValue(data)

    /**
     * 结束更新，并传入数据
     * @param data 数据
     */
    fun finishWithResponse(data: ResponseData<List<T>?>?) {
        this.data.setValue(data?.data)
        data?.let {
            // 请求失败，显示异常信息
            if (!it.isSucceed) GetTip.show(it.message)
        }
    }

    /**
     * 结束更新，并传入异常信息
     * @param throwable 异常信息
     */
    fun finishWithException(throwable: Throwable?) {
        data.setValue(null)
        stateDelegate.setError(throwable?.message)
    }

    /** 自动刷新数据 **/
    fun refreshAutomatic() {
        if (firstRefresh) {
            stateDelegate.setBusy(null)
            if (refreshLayout == null) refreshSilent()
            else refreshLayout?.post { refreshSilent() }
        } else {
            if (refreshLayout == null) refreshSilent()
            else refreshLayout?.autoRefresh()
        }
    }

    /** 静默刷新数据 **/
    fun refreshSilent() {
        currentPageNum = 1
        callbacks.forEach { it.invoke(currentPageNum, defaultPageSize) }
    }
}