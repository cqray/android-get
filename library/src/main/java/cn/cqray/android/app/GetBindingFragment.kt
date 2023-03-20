package cn.cqray.android.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cn.cqray.android.Get
import cn.cqray.android.util.ReflectUtil
import cn.cqray.android.util.ViewUtils

@Suppress("MemberVisibilityCanBePrivate")
abstract class GetBindingFragment<VB : ViewBinding> : GetFragment() {

    val binding: VB by lazy { generateViewBinding() }

    open override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(binding.root)
    }

    private fun generateViewBinding(): VB {
//        val activity = GetManager.topActivity
//        val context = activity?: Get.context
//        val content = activity?.findViewById<ViewGroup>(android.R.id.content)
        val bindingClass = ReflectUtil.getActualTypeArgument(this.javaClass)
        val method = bindingClass!!.getMethod(
            "inflate",
            LayoutInflater::class.java,
//            ViewGroup::class.java,
//            Boolean::class.java
        )
        return method.invoke(null, layoutInflater) as VB
    }
}