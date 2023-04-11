package cn.cqray.android.app

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import cn.cqray.android.util.ReflectUtils

/**
 * 支持[ViewBinding]的[GetActivity]
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast"
)
abstract class GetBindingActivity<VB : ViewBinding> : GetActivity() {

    val binding: VB by lazy { generateViewBinding() }

    open override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(binding.root)
    }

    private fun generateViewBinding(): VB {
        val bindingClass = ReflectUtils.getActualTypeArgument(this.javaClass)
        val method = bindingClass!!.getMethod("inflate", LayoutInflater::class.java,)
        return method.invoke(null, layoutInflater) as VB
    }
}