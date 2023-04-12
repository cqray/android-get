package cn.cqray.android.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cn.cqray.android.util.ReflectUtils
import cn.cqray.android.util.Views
import com.chad.library.adapter.base.BaseQuickAdapter

@Suppress(
    "Unchecked_cast",
    "Unused"
)
abstract class BindingAdapter<T, VB : ViewBinding> : BaseQuickAdapter<T, BindingViewHolder<VB>>(0) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        val clazz = ReflectUtils.getActualTypeArgument(javaClass, 1) as Class<VB>
        val binding = Views.getBinding(clazz, parent)
        return BindingViewHolder(binding)
    }

}