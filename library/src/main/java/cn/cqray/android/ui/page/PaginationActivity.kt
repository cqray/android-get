package cn.cqray.android.ui.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqray.android.app.GetActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 分页Activity
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
abstract class PaginationActivity<T> : GetActivity(), PaginationProvider<T> {

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
    override val paginationDelegate by lazy { PaginationDelegate<T>(this) }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(recyclerView)
        // 初始化列表
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        // 初始化分页委托
        paginationDelegate.setRefreshLayout(refreshLayout)
        paginationDelegate.adapter = adapter
        paginationDelegate.addCallback { pageNum, pageSize -> onRefresh(pageNum, pageSize) }
        refreshAutomatic()
    }

    /**
     * 创建Adapter
     * @return Adapter对象
     */
    protected abstract fun onCreateAdapter(): BaseQuickAdapter<T, out BaseViewHolder>

    /**
     * 刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected abstract fun onRefresh(pageNum: Int, pageSize: Int)
}