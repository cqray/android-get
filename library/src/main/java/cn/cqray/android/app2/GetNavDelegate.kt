package cn.cqray.android.app2

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.lifecycle.GetViewModelProvider
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashMap

/**
 * Get框架导航委托
 * @author Cqray
 */
class GetNavDelegate(private val navProvider: GetNavProvider) {

    init {
        GetUtils.checkProvider(navProvider)
        navDelegates[navProvider] = this
    }

    private var navViewModel: GetNavViewModel? = null

    private val mHandler = Handler(Looper.getMainLooper())

    val viewModel: GetNavViewModel
        get() {
            if (navViewModel == null) {
                throw IllegalStateException("The [GetNavViewModel] couldn't be used when Lifecycle state is initialized.")
            }
            return navViewModel!!
        }

    /**
     * 主要是初始化[GetNavViewModel]以及管理[Activity.onBackPressed]事件
     */
    fun onCreated() {
        val activity: FragmentActivity
        if (navProvider is Fragment) {
            activity = (navProvider as Fragment).requireActivity()
        } else {
            activity = navProvider as FragmentActivity
            activity.onBackPressedDispatcher.addCallback(activity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // 处理回退事件
                        viewModel.onBackPressed()
                    }
                })
        }
        // 初始化GetNavViewModel
        navViewModel = GetViewModelProvider(activity).get(GetNavViewModel::class.java)
    }

    /** 回收资源 **/
    fun onDestroyed() {
        // 保证其他资源回收后，回收自身资源
        mHandler.removeCallbacksAndMessages(null)
        navDelegates.remove(navProvider)
    }

    /**
     * 主要实现[GetNavProvider.onEnterAnimEnd]回调
     */
    fun onViewCreated() {
        val enterAnimDuration: Int = if (navProvider is Fragment) {
            navViewModel!!.enterAnimDuration
        } else {
            val animResId = GetUtils.getActivityOpenEnterAnimationResId((navProvider as Activity))
            GetUtils.getAnimDurationFromResource(animResId)
        }
        mHandler.postDelayed({ navProvider.onEnterAnimEnd() }, enterAnimDuration.toLong())
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent?) {
        viewModel.loadRootFragment(containerId, intent!!)
    }

    /**
     * 设置返回数据
     * @param data [Bundle]数据
     */
    fun setGetResult(data: Bundle?) = GetResultManager.sendToTopReceiver(data)

    /**
     * 启动界面
     * @param intent [GetIntent]
     */
    fun to(intent: GetIntent) = viewModel.to(intent)

    /**
     * 启动界面
     * @param intent [GetIntent]
     * @param callback [GetIntentCallback]回调
     */
    fun to(intent: GetIntent, callback: GetIntentCallback?) {
        viewModel.to(intent)
        if (callback != null) {
            GetResultManager.registerReceiver(lifecycleOwner, callback)
        }
    }

    /**
     * 回退界面
     */
    fun back() = viewModel.back()

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    fun backTo(back: Class<*>?, inclusive: Boolean) = viewModel.backTo(back, inclusive)

    val lifecycleOwner: LifecycleOwner get() = navProvider as LifecycleOwner

    companion object {
        /** 委托缓存 [GetNavDelegate] **/
        private val navDelegates =
            Collections.synchronizedMap(HashMap<GetNavProvider, GetNavDelegate>())

        /**
         * 获取并初始化[GetNavDelegate]
         * @param provider [GetNavProvider]实现对象
         */
        @JvmStatic
        fun get(provider: GetNavProvider): GetNavDelegate {
            var delegate = navDelegates[provider]
            synchronized(GetNavDelegate::class.java) {
                if (delegate == null) {
                    delegate = GetNavDelegate(provider)
                }
            }
            return delegate!!
        }
    }

}