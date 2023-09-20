package cn.cqray.android.lifecycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.app.GetNavProvider

/**
 * Get Fragment生命周期回调
 * @author Cqray
 */
class GetFragmentLifecycleCallbacks(activity: FragmentActivity) :
    FragmentManager.FragmentLifecycleCallbacks() {

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                    this@GetFragmentLifecycleCallbacks
                )
            }
        })
        // 注册回调监听
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(this, true)
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        if (f is GetNavProvider) {
            // GetNavDelegate调用onCreated()
            f.navDelegate.onCreated()
        }
    }
}