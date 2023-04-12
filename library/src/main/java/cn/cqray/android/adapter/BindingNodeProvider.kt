package cn.cqray.android.adapter

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode

abstract class BindingNodeProvider<VB : ViewBinding> : BindingItemProvider<BaseNode, VB>() {

    override fun getAdapter(): BaseNodeAdapter? {

        return super.getAdapter() as? BaseNodeAdapter
    }
}