package cn.cqray.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import java.lang.reflect.ParameterizedType

@Suppress("Unchecked_cast")
abstract class BaseBindingAdapter<T, VB : ViewBinding> : BaseQuickAdapter<T, BindingViewHolder<VB>>(0) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        val clazz = getViewBindingClass()!!
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        val inflater = LayoutInflater.from(parent.context)
        val binding = method.invoke(null, inflater, parent, false) as VB
        return BindingViewHolder(binding)
    }

    private fun getViewBindingClass(): Class<*>? {
        val type = this.javaClass.genericSuperclass
        if (type is ParameterizedType) {
            return type.actualTypeArguments.getOrNull(1) as Class<*>
        }
        return null
    }
}