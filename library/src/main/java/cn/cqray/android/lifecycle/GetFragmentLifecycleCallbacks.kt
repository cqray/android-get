package cn.cqray.android.lifecycle

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.app.GetManager
import cn.cqray.android.app.GetNavProvider
import cn.cqray.android.log.GetLog

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

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        printFragmentStateLog(f, "onFragmentAttached")
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        printFragmentStateLog(f, "onFragmentCreated")
        if (f is GetNavProvider) {
            // GetNavDelegate调用onCreated()
            f.navDelegate.onCreated()
        }
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        printFragmentStateLog(f, "onFragmentViewCreated")
        if (f is GetNavProvider) {
            // GetNavDelegate调用onViewCreated()
            f.navDelegate.onViewCreated()
        }
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        printFragmentStateLog(f, "onFragmentStarted")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        printFragmentStateLog(f, "onFragmentResumed")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
        printFragmentStateLog(f, "onFragmentPaused")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        printFragmentStateLog(f, "onFragmentStopped")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        printFragmentStateLog(f, "onFragmentViewDestroyed")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        printFragmentStateLog(f, "onFragmentDestroyed")
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        printFragmentStateLog(f, "onFragmentDetached")
    }

    /**
     * 打印Activity状态日志
     * @param fragment [Fragment]
     * @param state 状态信息
     */
    private fun printFragmentStateLog(fragment: Fragment, state: String) {
        // 获取日志初始化配置
        val logInit = Get.init.logInit!!
        // 未启用则不继续
        if (logInit.fragmentLifecycleLogEnable == false) return
        // 打印日志
        GetLog.d(
            GetManager.javaClass,
            String.format(
                "%s [%d] -> %s",
                fragment.javaClass.name,
                fragment.hashCode(),
                state
            )
        )
    }
}