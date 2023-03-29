package cn.cqray.android.ui.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqray.android.app.GetFragment
import cn.cqray.android.`object`.ResponseData
import cn.cqray.android.util.ViewUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 分页Fragment
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused_parameter",
    "Unused"
)
abstract class GetPaginationFragment<T> : GetFragment(), GetPaginationProvider<T> {

    /** [RecyclerView]视图 **/
    val recyclerView by lazy {
        RecyclerView(requireContext()).also {
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.requestLayout()
        ViewUtils.closeRvAnimator(recyclerView)
        // 初始化分页委托
        paginationDelegate.setRefreshLayout(refreshLayout)
        paginationDelegate.adapter = adapter
        paginationDelegate.addCallback { pageNum, pageSize ->
            // LiveData方式请求
            onRefreshLd(pageNum, pageSize)?.let {
                it.observe(this) { ld -> finish(ld) }
                return@let
            }
            // Rx方式请求
            onRefreshRx(pageNum, pageSize)?.let { observable ->
                // 请求列表
                val d = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ finishWithResponse(it) }) { finishWithException(it) }
                addDisposable(d)
                return@let
            }
            // 其他方式请求
            onRefresh(pageNum, pageSize)
        }
    }

    override fun onLazyLoad() {
        super.onLazyLoad()
        refreshAutomatic()
    }

    /**
     * 创建Adapter
     * @return Adapter对象
     */
    protected abstract fun onCreateAdapter(): BaseQuickAdapter<T, out BaseViewHolder>

    /**
     * RxJava方式刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected open fun onRefreshRx(pageNum: Int, pageSize: Int): Observable<ResponseData<List<T>?>>? = null

    /**
     * RxJava方式刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected open fun onRefreshLd(pageNum: Int, pageSize: Int): LiveData<List<T>?>? = null

    /**
     * 刷新数据
     * @param pageNum 页码
     * @param pageSize 分页大小
     */
    protected open fun onRefresh(pageNum: Int, pageSize: Int) {}
}