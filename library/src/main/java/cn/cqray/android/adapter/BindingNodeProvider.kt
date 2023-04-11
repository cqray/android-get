package cn.cqray.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cn.cqray.android.util.ReflectUtils
import cn.cqray.android.util.Views
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

@Suppress(
    "Unchecked_cast",
    "Unused"
)
abstract class BindingNodeProvider<VB : ViewBinding> : BaseNodeProvider() {

    override val layoutId: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val clazz = ReflectUtils.getActualTypeArgument(javaClass, 0) as Class<VB>
        val binding = Views.getBinding(clazz, parent)
        return BindingViewHolder(binding)
    }

    override fun onViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        onViewHolderCreated(viewHolder as BindingViewHolder<VB>, viewType)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        onViewAttachedToWindow(holder as BindingViewHolder<VB>)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        onViewDetachedFromWindow(holder as BindingViewHolder<VB>)
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        convert(helper as BindingViewHolder<VB>, item)
    }
    override fun convert(helper: BaseViewHolder, item: BaseNode, payloads: List<Any>) {
        convert(helper as BindingViewHolder<VB>, item, payloads)
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        onClick(helper as BindingViewHolder<VB>, view, data, position)
    }

    override fun onLongClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int): Boolean {
        return onLongClick(helper as BindingViewHolder<VB>, view, data, position)
    }

    override fun onChildClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        onChildClick(helper as BindingViewHolder<VB>, view, data, position)
    }

    override fun onChildLongClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int): Boolean {
        return onChildLongClick(helper as BindingViewHolder<VB>, view, data, position)
    }

    open fun onViewHolderCreated(holder: BindingViewHolder<VB>, viewType: Int) {}

    open fun onViewAttachedToWindow(holder: BindingViewHolder<VB>) {}

    open fun onViewDetachedFromWindow(holder: BindingViewHolder<VB>) {}

    abstract fun convert(holder: BindingViewHolder<VB>, item: BaseNode)

    open fun convert(holder: BindingViewHolder<VB>, item: BaseNode, payloads: List<Any>) {}

    open fun onClick(holder: BindingViewHolder<VB>, view: View, data: BaseNode, position: Int) {}

    open fun onLongClick(holder: BindingViewHolder<VB>, view: View, data: BaseNode, position: Int): Boolean = false

    open fun onChildClick(holder: BindingViewHolder<VB>, view: View, data: BaseNode, position: Int) {}

    open fun onChildLongClick(holder: BindingViewHolder<VB>, view: View, data: BaseNode, position: Int): Boolean = false
}