package cn.cqray.android.ui.page

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqray.android.app.GetFragment
import cn.cqray.android.databinding.GetPaginationLayoutBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 分页Fragment
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
abstract class PaginationFragment<T> : GetFragment(), PaginationProvider<T> {

//    /** [RecyclerView]视图 **/
//    val recyclerView by lazy {
//        RecyclerView(requireContext()).also {
////            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
//            it.overScrollMode = View.OVER_SCROLL_NEVER
//            it.setBackgroundColor(Color.CYAN)
//        }
//    }

    private val binding by lazy { GetPaginationLayoutBinding.inflate(layoutInflater) }

    val recyclerView by lazy {binding.root as RecyclerView}

    /** 数据适配器 **/
    val adapter by lazy { onCreateAdapter() }

    /** 分页委托 **/
    override val paginationDelegate by lazy { PaginationDelegate<T>(this) }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(recyclerView)
        // 初始化列表
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
//        recyclerView.layoutParams = ViewGroup.LayoutParams(-1, -1)
//        recyclerView.requestLayout()
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