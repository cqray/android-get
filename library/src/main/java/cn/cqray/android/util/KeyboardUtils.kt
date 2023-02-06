package cn.cqray.android.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

import cn.cqray.android.Get.context
import kotlin.math.abs


object KeyboardUtils {

    /** 全局监听 TAG **/
    private const val TAG_ON_LAYOUT_LISTENERS = -8

    /** 未显示高度 TAG **/
    private const val TAG_ON_INVISIBLE_HEIGHT = -9

//    /** StatusBar高度 **/
//    val statusBarHeight: Int
//        get() {
//            val resources = context.resources
//            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
//            return resources.getDimensionPixelSize(resourceId)
//        }
//
//    /** NavigationBar高度 **/
//    val navBarHeight: Int
//        get() {
//            val res = context.resources
//            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
//            return if (resourceId != 0) res.getDimensionPixelSize(resourceId) else 0
//        }

    /**
     * 显示软键盘
     */
    @JvmStatic
    fun showSoftInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 显示软键盘
     */
    fun showSoftInput(activity: Activity?) {
        if (activity == null) {
            return
        }
        if (!isSoftInputVisible(activity)) {
            toggleSoftInput()
        }
    }
    /**
     * 显示软键盘
     * @param view The view
     * @param flags Provides additional operating flags.  Currently may be
     * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
     */
    /**
     * 显示软键盘
     * @param view The view.
     */
    @JvmOverloads
    fun showSoftInput(view: View, flags: Int = 0) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm.showSoftInput(view, flags, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
                    || resultCode == InputMethodManager.RESULT_HIDDEN
                ) {
                    toggleSoftInput()
                }
            }
        })
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 隐藏软键盘
     * @param activity The activity.
     */
    fun hideSoftInput(activity: Activity?) {
        if (activity == null) {
            return
        }
        hideSoftInput(activity.window)
    }

    /**
     * 隐藏软键盘
     * @param window The window.
     */
    fun hideSoftInput(window: Window?) {
        if (window == null) {
            return
        }
        var view = window.currentFocus
        if (view == null) {
            val decorView = window.decorView
            val focusView = decorView.findViewWithTag<View>("keyboardTagView")
            if (focusView == null) {
                view = EditText(window.context)
                view.setTag("keyboardTagView")
                (decorView as ViewGroup).addView(view, 0, 0)
            } else {
                view = focusView
            }
            view.requestFocus()
        }
        hideSoftInput(view)
    }

    /**
     * 隐藏软键盘
     * @param view The view.
     */
    fun hideSoftInput(view: View?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm == null || view == null) {
            return
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 切换软键盘状态
     */
    fun toggleSoftInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        imm.toggleSoftInput(0, 0)
    }

    private var sDecorViewDelta = 0

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSoftInputVisible(activity: Activity): Boolean {
        return getDecorViewInvisibleHeight(activity.window) > 0
    }

    /**
     * 获取DecorView未显示的高度
     */
    private fun getDecorViewInvisibleHeight(window: Window?): Int {
        window?.let {
            val outRect = Rect().also { rect -> window.decorView.getWindowVisibleDisplayFrame(rect) }
//            it.decorView.getWindowVisibleDisplayFrame(outRect)
//            Log.d("KeyboardUtils", "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom))
            val delta = abs(it.decorView.bottom - outRect.bottom)
            if (delta <= ScreenUtils.navBarHeight + ScreenUtils.statusBarHeight) {
                sDecorViewDelta = delta
                return 0
            }
            delta - sDecorViewDelta
        }
        return 0
    }

    /**
     * 订阅软键盘高度变化
     * @param activity [Activity]
     * @param observer 观察者
     */
    @Suppress("Unchecked_cast")
    fun observeSoftInputChanged(activity: ComponentActivity?, observer: Observer<Int>?) {
        activity?.window?.let {
            val flags = it.attributes.flags
            if ((flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
                it.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            val contentView = activity.findViewById<View>(android.R.id.content) ?: return
            // 初始化监听列表
            val listeners = (contentView.getTag(TAG_ON_LAYOUT_LISTENERS) as? MutableList<OnGlobalLayoutListener>)
                ?: mutableListOf()
            // 回调函数
            val glListener = OnGlobalLayoutListener {
                val oldHeight = contentView.getTag(TAG_ON_INVISIBLE_HEIGHT) as Int? ?: 0
                val newHeight = getContentViewInvisibleHeight(it)
                // 通知键盘变化
                if (oldHeight != newHeight) {
                    observer?.onChanged(newHeight)
                    contentView.setTag(TAG_ON_INVISIBLE_HEIGHT, newHeight)
                }
            }
            // 监听信息
            contentView.viewTreeObserver.addOnGlobalLayoutListener(glListener)
            // 缓存相关信息
            contentView.setTag(TAG_ON_INVISIBLE_HEIGHT, getContentViewInvisibleHeight(it))
            contentView.setTag(TAG_ON_LAYOUT_LISTENERS, listeners.also { listeners.add(glListener) })
            // 自动销毁，防止内容泄漏
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    // 获取监听列表
                    val list = contentView.getTag(TAG_ON_LAYOUT_LISTENERS) as? MutableList<OnGlobalLayoutListener>
                    list?.let { it ->
                        // 移除监听事件
                        it.forEach { listener -> contentView.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
                        it.clear()
                        //这里会发生内存泄漏 如果不设置为null
                        contentView.setTag(TAG_ON_LAYOUT_LISTENERS, null)
                    }
                }
            })
        }
    }

    /**
     * Fix the bug of 5497 in Android.
     *
     * Don't set adjustResize
     *
     * @param activity The activity.
     */
    fun fixAndroidBug5497(activity: Activity) {
        fixAndroidBug5497(activity.window)
    }

    /**
     * Fix the bug of 5497 in Android.
     *
     * It will clean the adjustResize
     *
     * @param window The window.
     */
    @Suppress("Deprecation")
    fun fixAndroidBug5497(window: Window) {
        // 清除 ADJUST_RESIZE 属性
        val softInputMode = window.attributes.softInputMode
        window.setSoftInputMode(softInputMode and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE.inv())

        val contentView = window.findViewById<FrameLayout>(android.R.id.content)
        val contentViewChild = contentView.getChildAt(0)
        val paddingBottom = contentViewChild.paddingBottom
        val contentViewInvisibleHeightPre5497 = intArrayOf(getContentViewInvisibleHeight(window))
        contentView.viewTreeObserver.addOnGlobalLayoutListener {
            val height = getContentViewInvisibleHeight(window)
            if (contentViewInvisibleHeightPre5497[0] != height) {
                contentViewChild.setPadding(
                    contentViewChild.paddingLeft,
                    contentViewChild.paddingTop,
                    contentViewChild.paddingRight,
                    paddingBottom + getDecorViewInvisibleHeight(window)
                )
                contentViewInvisibleHeightPre5497[0] = height
            }
        }
    }

    private fun getContentViewInvisibleHeight(window: Window): Int {
        val contentView = window.findViewById<View>(android.R.id.content) ?: return 0
        val outRect = Rect().also { window.decorView.getWindowVisibleDisplayFrame(it) }
        val delta = abs(contentView.bottom - outRect.bottom)
        return if (delta <= ScreenUtils.statusBarHeight + ScreenUtils.navBarHeight) 0 else delta
    }

    /**
     * 修复软键盘输入泄漏问题
     * @param activity The activity.
     */
    fun fixSoftInputLeaks(activity: Activity) {
        fixSoftInputLeaks(activity.window)
    }

    /**
     * 修复软键盘输入泄漏问题
     * @param window The window.
     */
    fun fixSoftInputLeaks(window: Window) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            try {
                val leakViewField = InputMethodManager::class.java.getDeclaredField(leakView)
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField[imm] as? View ?: continue
                if (obj.rootView === window.decorView.rootView) {
                    leakViewField[imm] = null
                }
            } catch (ignore: Throwable) { /**/
            }
        }
    }


}