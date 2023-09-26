package cn.cqray.android.ui.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqray.android.app.GetActivity
import cn.cqray.android.`object`.ResponseData
import cn.cqray.android.util.Check3rdUtils
import cn.cqray.android.util.Views
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 分页Activity
 * @author Cqray
 */
@OptIn(DelicateCoroutinesApi::class)
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
abstract class GetPaginationActivity<T> : GetActivity(), GetPaginationProvider<T> {

    /** 任务集合 **/
    private val jobs = mutableListOf<Any>()

    /** [RecyclerView]视图 **/
    val recyclerView by lazy {
        RecyclerView(this).also {
            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    /** 数据适配器 **/
    val adapter by lazy { onCreateAdapter() }

    /** 分页委托 **/
    override val paginationDelegate by lazy { GetPaginationDelegate<T>(this) }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(recyclerView)
        // 初始化列表
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        Views.closeRvAnimator(recyclerView)
        // 初始化分页委托
        paginationDelegate.attachRefreshLayout(refreshLayout)
        paginationDelegate.adapter = adapter
        paginationDelegate.addCallback { pageNum, pageSize ->
            // 是否支持协程
            if (Check3rdUtils.isCoroutinesSupport) {
                val job = GlobalScope.launch {
                    val data = onRefreshSuspend(pageNum, pageSize)
                    // data为null，说明没有使用协程
                    if (data == null) onRefresh(pageNum, pageSize)
                    // 不为null，则处理响应体
                    else finishWithResponse(data)
                }
                jobs.add(job)
            } else {
                // 其他方式请求
                onRefresh(pageNum, pageSize)
            }
        }
    }

    override fun onEnterAnimEnd() {
        super.onEnterAnimEnd()
        refreshAutomatic()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Check3rdUtils.isCoroutinesSupport) {
            jobs.forEach { (it as Job).cancel() }
            jobs.clear()
        }
    }

    /**
     * 创建Adapter
     * @return Adapter对象
     */
    protected abstract fun onCreateAdapter(): BaseQuickAdapter<T, out BaseViewHolder>

    /**
     * 以挂起的方式刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected open suspend fun onRefreshSuspend(pageNum: Int, pageSize: Int): ResponseData<List<T>>? = null

    /**
     * 刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected open fun onRefresh(pageNum: Int, pageSize: Int) {}
}