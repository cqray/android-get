package cn.cqray.android.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter

@Suppress(
    "Unchecked_cast"
)
abstract class BindingAdapter<T, VB : ViewBinding>(
    private val clazz: Class<VB>
) : BaseQuickAdapter<T, BindingViewHolder<VB>>(0) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
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
}