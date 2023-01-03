package cn.cqray.android.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class GetViewModelProvider : ViewModelProvider {
    constructor(owner: ViewModelStoreOwner) : super(owner, GetViewModelFactory(owner))
    constructor(fragment: Fragment) : super(fragment, GetViewModelFactory(fragment))
    constructor(activity: FragmentActivity) : super(activity, GetViewModelFactory(activity))
}