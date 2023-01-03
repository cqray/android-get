package cn.cqray.android.lifecycle

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.manage.GetActivityManager
import cn.cqray.android.app2.GetNavDelegate.Companion.get
import cn.cqray.android.app2.GetNavProvider
import cn.cqray.android.log.GetLog

/**
 * Get Fragment生命周期回调
 * @author Cqray
 */
class GetFragmentLifecycleCallbacks(activity: FragmentActivity) :
    FragmentManager.FragmentLifecycleCallbacks() {

    init {
        activity.lifecycle.addObserver(LifecycleEventObserver { _: LifecycleOwner?, event: Lifecycle.Event ->
            // 注销回调监听
            if (event == Lifecycle.Event.ON_DESTROY) {
                handler.removeCallbacksAndMessages(null)
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(this)
            }
        })
        // 注册回调监听
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(this, true)
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        // 打印日志
        printFragmentStateLog(f, "onFragmentAttached")
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        // 打印日志
        printFragmentStateLog(f, "onFragmentCreated")
        if (f is GetNavProvider) {
            get((f as GetNavProvider)).onCreated()
        }
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        // 打印日志
        printFragmentStateLog(f, "onFragmentViewCreated")
        if (f is GetNavProvider) {
            get((f as GetNavProvider)).onViewCreated()
        }
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        // 打印日志
        printFragmentStateLog(f, "onFragmentStarted")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        // 打印日志
        printFragmentStateLog(f, "onFragmentResumed")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
        // 打印日志
        printFragmentStateLog(f, "onFragmentPaused")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        // 打印日志
        printFragmentStateLog(f, "onFragmentStopped")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        // 打印日志
        printFragmentStateLog(f, "onFragmentViewDestroyed")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        // 打印日志
        printFragmentStateLog(f, "onFragmentDestroyed")
        if (f is GetNavProvider) {
            // 在post中，保证GetNavDelegate的资源最后回收
            handler.post { get((f as GetNavProvider)).onDestroyed() }
        }
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        // 打印日志
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
            GetActivityManager.javaClass,
            String.format(
                "%s [%d] -> %s",
                fragment.javaClass.name,
                fragment.hashCode(),
                state
            )
        )
    }
}