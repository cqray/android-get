package cn.cqray.android.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.anim.AnimUtils
import cn.cqray.android.lifecycle.GetViewModelProvider
import cn.cqray.android.lifecycle.GetViewModel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Get框架导航委托
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetNavDelegate(provider: GetNavProvider) : GetDelegate<GetNavProvider>(provider) {

    /** 是否已懒加载 **/
    private val isLazyLoad = AtomicBoolean()

    /** [LifecycleOwner]生命周期管理持有 **/
    val lifecycleOwner by lazy { provider as LifecycleOwner }

    /** 导航[GetViewModel] **/
    private val viewModel by lazy { GetViewModelProvider(activity).get(GetNavViewModel::class.java) }

    /** [FragmentActivity]实例 **/
    val activity by lazy {
        if (provider is FragmentActivity) provider
        else (provider as Fragment).requireActivity()
    }

    /** 栈顶Fragment **/
    val topFragment get() = viewModel.topFragment

    /** 回退栈Fragments **/
    val fragments get() = viewModel.fragments

    /**
     * 主要是初始化[GetNavViewModel]以及管理[Activity.onBackPressed]事件
     */
    internal fun onCreated() {
        if (provider is FragmentActivity) {
            // 监听Activity的back事件
            provider.onBackPressedDispatcher.addCallback(provider,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        // 处理回退事件
                        viewModel.onBackPressed()
                    }
                })
        }
        // 懒加载实现
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                // 懒加载实现
                if (!isLazyLoad.get()) {
                    provider.onLazyLoad()
                    isLazyLoad.set(true)
                }
            }
        })
        // 等待动画结束
        waitEnterAnimEnd()
    }

    /**
     * 等待Fragment、Activity进入动画结束
     * 实现[GetNavProvider.onEnterAnimEnd]回调
     */
    private fun waitEnterAnimEnd() {
        // 获取动画时长
        val enterAnimDuration: Int = if (provider is Fragment) {
            viewModel.getFragmentEnterAnimDuration(provider)
        } else {
            val animResId = AnimUtils.getActivityOpenEnterAnimResId((provider as Activity))
            AnimUtils.getAnimDurationFromResource(animResId)
        }
        // 动画结束回调
        Get.runOnUiThreadDelayed({ provider.onEnterAnimEnd() }, enterAnimDuration)
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param rootClass 根Fragment类
     */
    fun loadRootFragment(@IdRes containerId: Int, rootClass: Class<*>) {
        viewModel.loadRootFragment(containerId, GetIntent(rootClass))
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent) {
        viewModel.loadRootFragment(containerId, intent)
    }

    /**
     * 设置返回数据
     * @param data [Bundle]数据
     */
    fun setResult(data: Bundle) = GetResultHelper.sendToTopReceiver(data)

    /**
     * 启动界面
     * @param toClass 目标Class
     */
    fun to(toClass: Class<*>) = viewModel.to(GetIntent(toClass))

    /**
     * 启动界面
     * @param intent [GetIntent]
     */
    fun to(intent: GetIntent) = viewModel.to(intent)

    /**
     * 启动界面
     * @param intent 意图
     * @param callback 回调
     */
    fun to(intent: GetIntent, callback: Function1<Bundle, Unit>?) {
        viewModel.to(intent)
        callback?.let { GetResultHelper.registerReceiver(lifecycleOwner, it) }
    }

    /**
     * 回退界面
     */
    fun back() = viewModel.back()

    /**
     * 回退到指定的界面（包含自身）
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     */
    fun backTo(back: Class<*>) = viewModel.backTo(back, true)

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    fun backTo(back: Class<*>, inclusive: Boolean) = viewModel.backTo(back, inclusive)
}