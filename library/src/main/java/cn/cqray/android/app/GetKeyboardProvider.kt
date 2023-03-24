package cn.cqray.android.app

import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.cqray.android.Get
import com.blankj.utilcode.util.KeyboardUtils

/**
 * 软键盘控制提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetKeyboardProvider {

    /**
     * 显示软键盘
     */
    fun showSoftInput() = Get.runOnUiThreadDelayed({ KeyboardUtils.showSoftInput(Get.topActivity) }, 100)

    /**
     * 获取焦点，并显示软键盘
     * @param view 要获取焦点的控件
     */
    fun showSoftInput(view: View) = Get.runOnUiThreadDelayed({ KeyboardUtils.showSoftInput(view) }, 100)

    /**
     * 隐藏软键盘
     */
    fun hideSoftInput() = KeyboardUtils.hideSoftInput(Get.topActivity)

    /**
     * 切换软件盘状态
     */
    fun toggleSoftInput() = KeyboardUtils.toggleSoftInput()

    /**
     * 订阅软键盘高度变化
     */
    fun observeSoftInputChanged(fragment: Fragment, observer: Observer<Int>) {
        fragment.activity?.let {
            // 注册键盘高度变化监听
            KeyboardUtils.registerSoftInputChangedListener(it.window) { h -> observer.onChanged(h) }
            // 自动销毁
            fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    KeyboardUtils.unregisterSoftInputChangedListener(it.window)
                }
            })
        }
    }

    /**
     * 订阅软键盘高度变化
     */
    fun observeSoftInputChanged(activity: ComponentActivity, observer: Observer<Int>) {
        // 注册键盘高度变化监听
        KeyboardUtils.registerSoftInputChangedListener(activity.window) { observer.onChanged(it) }
        // 自动销毁
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                KeyboardUtils.unregisterSoftInputChangedListener(activity.window)
            }
        })
    }
}