package cn.cqray.android.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.lifecycle.GetViewModelProvider
import cn.cqray.android.lifecycle.GetViewModel
import java.lang.IllegalStateException

/**
 * Get框架导航委托
 * @author Cqray
 */
class GetNavDelegate(provider: GetNavProvider) : GetDelegate<GetNavProvider>(provider) {

    private var navViewModel: GetNavViewModel? = null

    /** [LifecycleOwner]生命周期管理持有 **/
    val lifecycleOwner: LifecycleOwner get() = provider as LifecycleOwner

    /** 导航[GetViewModel] **/
    val viewModel: GetNavViewModel
        get() {
            if (navViewModel == null) throw IllegalStateException(
                "The [GetNavViewModel] couldn't be used when Lifecycle state is not after created."
            )
            return navViewModel!!
        }

    /**
     * 主要是初始化[GetNavViewModel]以及管理[Activity.onBackPressed]事件
     */
    internal fun onCreated() {
        val activity: FragmentActivity
        if (provider is Fragment) {
            activity = (provider as Fragment).requireActivity()
        } else {
            activity = provider as FragmentActivity
            // 监听Activity的back事件
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

    /**
     * 主要实现[GetNavProvider.onEnterAnimEnd]回调
     */
    internal fun onViewCreated() {
        // 获取动画时长
        val enterAnimDuration: Int = if (provider is Fragment) {
            navViewModel!!.enterAnimDuration
        } else {
            val animResId = GetUtils.getActivityOpenEnterAnimationResId((provider as Activity))
            GetUtils.getAnimDurationFromResource(animResId)
        }
        // 动画结束回调
        GetManager.runOnUiThreadDelayed({ provider.onEnterAnimEnd() }, enterAnimDuration)
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
        callback?.let { GetResultManager.registerReceiver(lifecycleOwner, callback) }
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
    fun backTo(back: Class<*>?, inclusive: Boolean?) = viewModel.backTo(back, inclusive)
}