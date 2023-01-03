package cn.cqray.android.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

/**
 * 持有[LifecycleOwner]的ViewModel
 * 需要使用[GetViewModelProvider]获取
 * @author Cqray
 */
open class GetViewModel(val lifecycleOwner: LifecycleOwner) : ViewModel(), DefaultLifecycleObserver