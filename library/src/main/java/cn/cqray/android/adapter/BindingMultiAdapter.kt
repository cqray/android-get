package cn.cqray.android.adapter

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity

abstract class BindingMultiAdapter<T : MultiItemEntity, VB : ViewBinding> :
    BaseMultiItemQuickAdapter<T, BindingViewHolder<VB>>() {}