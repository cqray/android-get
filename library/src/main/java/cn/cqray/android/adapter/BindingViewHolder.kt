package cn.cqray.android.adapter

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

@Suppress("MemberVisibilityCanBePrivate")
class BindingViewHolder<VB : ViewBinding>(val binding: VB) : BaseViewHolder(binding.root)