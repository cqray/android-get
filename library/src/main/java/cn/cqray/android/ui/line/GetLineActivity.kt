package cn.cqray.android.ui.line

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqray.android.app2.GetActivity
import cn.cqray.android.util.Views

/**
 * 列型UI界面
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "NotifyDataSetChanged",
    "Unused",
)
open class GetLineActivity : GetActivity() {

    /** 行适配器 **/
    val lineAdapter by lazy { GetLineAdapter() }

    /** 列表容器 **/
    val recyclerView by lazy { RecyclerView(this) }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(recyclerView.also {
            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = lineAdapter
            it.overScrollMode = View.OVER_SCROLL_NEVER
        })
        Views.closeRvAnimator(recyclerView)
    }

    fun addLineItem(item: GetLineItem<*>) = lineAdapter.addData(item)

    fun setLineItems(items: Collection<GetLineItem<*>>) = lineAdapter.setList(items)

    fun getLineItem(index: Int): GetLineItem<*>? = lineAdapter.getItemOrNull(index)

    fun getLineItemByTag(tag: Any?): GetLineItem<*>? = lineAdapter.getItemByTag(tag)

    fun notifyDataSetChanged() = lineAdapter.notifyDataSetChanged()
}