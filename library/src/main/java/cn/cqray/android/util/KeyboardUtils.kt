package cn.cqray.android.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
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
import cn.cqray.android.app.GetManager
import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate", "Unused")
object KeyboardUtils {

    /** 全局监听 TAG **/
    private const val TAG_ON_LAYOUT_LISTENERS = -7

    /** 未显示高度 TAG **/
    private const val TAG_ON_INVISIBLE_HEIGHT = -8

    /** 软键盘管理器 **/
    private val imm get() = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

    /**
     * 显示软键盘
     */
    @JvmStatic
    fun showSoftInput() = showSoftInput(GetManager.topActivity)

    /**
     * 显示软键盘
     */
    @JvmStatic
    fun showSoftInput(activity: Activity?) = showSoftInput(activity?.window)

    /**
     * 显示软键盘
     */
    @JvmStatic
    fun showSoftInput(window: Window?) = showSoftInput(window?.decorView)

    /**
     * 显示软键盘
     * @param view 视图
     */
    @JvmStatic
    fun showSoftInput(view: View?) = view?.let {
        val activity = ViewUtils.getActivity(view)
        if (!isSoftInputVisible(activity)) {
            if (view is EditText) {
                it.isFocusable = true
                it.isFocusableInTouchMode = true
                it.requestFocus()
            }
            imm?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    /**
     * 隐藏软键盘
     */
    @JvmStatic
    fun hideSoftInput() = hideSoftInput(GetManager.topActivity)

    /**
     * 隐藏软键盘
     * @param activity [Activity]
     */
    @JvmStatic
    fun hideSoftInput(activity: Activity?) = hideSoftInput(activity?.window)

    /**
     * 隐藏软键盘
     * @param window 窗体
     */
    @JvmStatic
    fun hideSoftInput(window: Window?) = window?.let { hideSoftInput(it.currentFocus) }

    /**
     * 隐藏软键盘
     * @param view 视图
     */
    @JvmStatic
    fun hideSoftInput(view: View?) = view?.windowToken?.let {
        val activity = ViewUtils.getActivity(view)
        if (isSoftInputVisible(activity)) {
            imm?.hideSoftInputFromWindow(it, 0)
        }
    }

    /**
     * 切换软键盘状态
     */
    @JvmStatic
    fun toggleSoftInput() = imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

    private var sDecorViewDelta = 0

//    /**
//     * Return whether soft input is visible.
//     *
//     * @param activity The activity.
//     * @return `true`: yes<br></br>`false`: no
//     */
//    fun isSoftInputVisible(activity: Activity): Boolean {
//        return getDecorViewInvisibleHeight(activity.window) > 0
//    }

    fun isSoftInputVisible(activity: Activity?) = getDecorViewInvisibleHeight(activity?.window) > 0

//    /**
//     * 获取DecorView未显示的高度
//     */
//    private fun getDecorViewInvisibleHeight(window: Window?): Int {
//        window?.let {
//            val outRect = Rect().also { rect -> window.decorView.getWindowVisibleDisplayFrame(rect) }
////            it.decorView.getWindowVisibleDisplayFrame(outRect)
////            Log.d("KeyboardUtils", "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom))
//            val delta = abs(it.decorView.bottom - outRect.bottom)
//            if (delta <= ScreenUtils.navBarHeight + ScreenUtils.statusBarHeight) {
//                sDecorViewDelta = delta
//                return 0
//            }
//            delta - sDecorViewDelta
//        }
//        return 0
//    }

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

    private fun getDecorViewInvisibleHeight(window: Window?): Int {
        return window?.let {
            val contentView = it.findViewById<View>(android.R.id.content) ?: return 0
            val outRect = Rect().also { rect -> it.decorView.getWindowVisibleDisplayFrame(rect) }
            val delta = abs(it.decorView.height - outRect.height())
            Log.e("数据", "${contentView.bottom}|${it.decorView.height}|${outRect.height()}|${outRect.bottom}|${delta}|${ScreenUtils.appScreenHeight}|${outRect.top}")
            if (delta <= ScreenUtils.statusBarHeight + ScreenUtils.navBarHeight) 0 else delta
        } ?: 0
    }

    private fun getContentViewInvisibleHeight(window: Window?): Int {
//        com.blankj.utilcode.util.KeyboardUtils;
        return window?.let {
            val contentView = it.findViewById<View>(android.R.id.content) ?: return 0
            val outRect = Rect().also { rect -> it.decorView.getWindowVisibleDisplayFrame(rect) }
            val delta = abs(contentView.bottom - outRect.bottom)
//            Log.e("数据", "${contentView.bottom}|${outRect.bottom}|${delta}")
            if (delta <= ScreenUtils.statusBarHeight + ScreenUtils.navBarHeight) 0 else delta
        } ?: 0
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