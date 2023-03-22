package cn.cqray.android.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.cqray.android.Get
import kotlin.math.abs

/**
 * 键盘工具类
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
object KeyboardUtils {

    /** 全局监听 TAG **/
    private const val TAG_ON_LAYOUT_LISTENERS = -7

    /** 未显示高度 TAG **/
    private const val TAG_ON_INVISIBLE_HEIGHT = -8

    /** 5497BUG修复监听 **/
    private const val TAG_ON_5497_BUG_FIX_LISTENER = -9

    /** 当前[Activity] **/
    private val currentActivity get() = Get.topActivity

    /** 软键盘管理器 **/
    private fun imm(context: Context?): InputMethodManager {
        val tmpContext = context ?: currentActivity ?: Get.context
        return tmpContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    /**
     * 显示软键盘
     */
    @JvmStatic
    fun showSoftInput() = showSoftInput(null as View?)

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
    fun showSoftInput(view: View?) {
        val activity = ViewUtils.view2Activity(view) ?: currentActivity
        if (!isSoftInputVisible(activity)) {
            if (view is EditText) {
                view.isFocusable = true
                view.isFocusableInTouchMode = true
                view.requestFocus()
            }
            toggleSoftInput(activity)
        }
    }

    /**
     * 隐藏软键盘
     */
    @JvmStatic
    fun hideSoftInput() = hideSoftInput(null as View?)

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
    fun hideSoftInput(view: View?) {
        val token = view?.windowToken
        if (token == null) {
            ViewUtils.view2Activity(view).let {
                // 如果软键盘显示状态，则切换状态
                if (isSoftInputVisible(it)) toggleSoftInput()
            }
        } else imm(view.context).hideSoftInputFromWindow(token, 0)
    }

    /**
     * 切换软键盘状态
     */
    @JvmStatic
    fun toggleSoftInput() = toggleSoftInput(null)

    /**
     * 切换软键盘状态
     */
    @JvmStatic
    fun toggleSoftInput(context: Context?) = imm(context).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    /**
     * 软键盘是否显示
     * @param activity [Activity]
     */
    @JvmStatic
    fun isSoftInputVisible(activity: Activity?) = getKeyboardHeight(activity) > 0

    /**
     * 订阅软键盘高度变化
     * @param activity [Activity]
     * @param observer 观察者
     */
    @JvmStatic
    @Suppress("Unchecked_cast")
    fun observeSoftInputChanged(activity: ComponentActivity?, observer: Observer<Int>?) {
        activity?.window?.let {
            val flags = it.attributes.flags
            if ((flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
                it.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            val contentView = activity.findViewById<View>(android.R.id.content) ?: return
            // 初始化监听列表
            val listeners = (contentView.getTag(TAG_ON_LAYOUT_LISTENERS) as? MutableList<OnGlobalLayoutListener>)
                ?: mutableListOf()
            // 回调函数
            val glListener = OnGlobalLayoutListener {
                val oldHeight = contentView.getTag(TAG_ON_INVISIBLE_HEIGHT) as Int? ?: 0
                val newHeight = getKeyboardHeight(activity)
                // 通知键盘变化
                if (oldHeight != newHeight) {
                    observer?.onChanged(newHeight)
                    contentView.setTag(TAG_ON_INVISIBLE_HEIGHT, newHeight)
                }
            }
            // 监听信息
            contentView.viewTreeObserver.addOnGlobalLayoutListener(glListener)
            // 缓存相关信息
            contentView.setTag(TAG_ON_INVISIBLE_HEIGHT, getContentViewInvisibleHeight(activity))
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
                        contentView.setTag(TAG_ON_INVISIBLE_HEIGHT, null)
                        contentView.setTag(TAG_ON_LAYOUT_LISTENERS, null)
                    }
                }
            })
        }
    }

    /**
     * 获取窗口未显示的高度
     */
    @JvmStatic
    fun getKeyboardHeight(activity: Activity?): Int {
        return activity?.window?.let {
            // 显示的矩形
            val visibleRect = Rect().also { rect -> it.decorView.getWindowVisibleDisplayFrame(rect) }
            // 底部导航栏占用高度
            val navBarHeight = if (ScreenUtils.isNavBarShown()) ScreenUtils.getNavBarHeight() else 0
            // 容器高度 - 显示的底部位置 - 减去底部导航栏的高度
            abs(it.decorView.height - visibleRect.bottom) - navBarHeight
        } ?: 0
    }

    /**
     * 获取[Activity]中[android.R.id.content]未实现高度
     * @param activity [Activity]
     */
    internal fun getContentViewInvisibleHeight(activity: Activity?): Int {
        return activity?.window?.let {
            val contentView = it.findViewById<View>(android.R.id.content) ?: return 0
            val outRect = Rect().also { rect -> it.decorView.getWindowVisibleDisplayFrame(rect) }
            abs(contentView.bottom - outRect.height())
        } ?: 0
    }

    /**
     * 修复软键盘5497BUG（软件盘挡住底部导航栏）
     * @param activity [Activity]
     */
    @Suppress("Deprecation")
    internal fun fixAndroidBug5497(activity: Activity) {
        // 获取窗口
        val window = activity.window ?: return
        // 清除 ADJUST_RESIZE 属性
        val softInputMode = window.attributes.softInputMode
        window.setSoftInputMode(softInputMode and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE.inv())

        // 获取组件属性
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val contentViewChild = contentView.getChildAt(0)
        val paddingBottom = contentViewChild.paddingBottom
        val contentViewInvisibleHeightPre5497 = intArrayOf(getContentViewInvisibleHeight(activity))
        // 监听事件
        val listener = contentView.getTag(TAG_ON_5497_BUG_FIX_LISTENER) as? OnGlobalLayoutListener
            ?: OnGlobalLayoutListener {
                val height = getContentViewInvisibleHeight(activity)
                if (contentViewInvisibleHeightPre5497[0] != height) {
                    runCatching {
                        contentViewChild.setPadding(
                            contentViewChild.paddingLeft,
                            contentViewChild.paddingTop,
                            contentViewChild.paddingRight,
                            paddingBottom + getKeyboardHeight(activity)
                        )
                        contentViewInvisibleHeightPre5497[0] = height
                    }
                }
            }
        contentView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        contentView.setTag(TAG_ON_5497_BUG_FIX_LISTENER, listener)
        // 自动销毁，防止内容泄漏
        if (activity is ComponentActivity) {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    // 获取监听列表
                    val ls = contentView.getTag(TAG_ON_5497_BUG_FIX_LISTENER) as? OnGlobalLayoutListener
                    ls?.let { it ->
                        // 移除监听事件
                        contentView.viewTreeObserver.removeOnGlobalLayoutListener(it)
                        //这里会发生内存泄漏 如果不设置为null
                        contentView.setTag(TAG_ON_5497_BUG_FIX_LISTENER, null)
                    }
                }
            })
        }
    }

    /**
     * 修复软键盘输入泄漏问题
     * @param activity [Activity]
     */
    @JvmStatic
    internal fun fixSoftInputLeaks(activity: Activity) {
        val imm = this.imm(activity)
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            runCatching {
                val leakViewField = InputMethodManager::class.java.getDeclaredField(leakView)
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField[imm] as? View
                if (obj?.rootView == activity.window.decorView.rootView) {
                    leakViewField[imm] = null
                }
            }
        }
    }
}