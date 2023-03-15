package cn.cqray.android.ui.line

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqray.android.app.GetFragment

/**
 * 列型UI界面
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused",
)
open class GetLineFragment : GetFragment() {

    /** 行适配器 **/
    val lineAdapter by lazy { GetLineAdapter() }

    /** 列表容器 **/
    val recyclerView by lazy { RecyclerView(requireContext()) }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(recyclerView.also {
            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = lineAdapter
            it.overScrollMode = View.OVER_SCROLL_NEVER
        })
    }

    fun addLineItem(item: GetLineItem<*>) = lineAdapter.addData(item)

    fun setLineItems(items: Collection<GetLineItem<*>>) = lineAdapter.setList(items)

    fun getLineItem(index: Int): GetLineItem<*>? = lineAdapter.getItemOrNull(index)

    fun getLineItemByTag(tag: Any?): GetLineItem<*>? = lineAdapter.getItemByTag(tag)

    fun notifyDataSetChanged() = lineAdapter.notifyDataSetChanged()
}