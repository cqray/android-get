package cn.cqray.android.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.math.abs

/**
 * 键盘工具类扩展
 * @author Cqray
 */
@Suppress("Unused")
object KeyboardUtil {

    /** 全局监听 TAG **/
    private const val TAG_ON_LAYOUT_LISTENERS = -100007

    /** 未显示高度 TAG **/
    private const val TAG_ON_INVISIBLE_HEIGHT = -100008



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
            val listeners = (contentView.getTag(TAG_ON_LAYOUT_LISTENERS) as? MutableList<ViewTreeObserver.OnGlobalLayoutListener>)
                ?: mutableListOf()
            // 回调函数
            val glListener = ViewTreeObserver.OnGlobalLayoutListener {
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
            contentView.setTag(
                TAG_ON_INVISIBLE_HEIGHT,
                getContentViewInvisibleHeight(activity)
            )
            contentView.setTag(TAG_ON_LAYOUT_LISTENERS, listeners.also { listeners.add(glListener) })
            // 自动销毁，防止内容泄漏
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    // 获取监听列表
                    val list = contentView.getTag(TAG_ON_LAYOUT_LISTENERS) as? MutableList<ViewTreeObserver.OnGlobalLayoutListener>
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
    @SuppressLint("InternalInsetResource")
    @JvmStatic
    fun getKeyboardHeight(activity: Activity?): Int {
        return activity?.window?.let {
            val navBarShown = com.blankj.utilcode.util.ScreenUtils.getAppScreenHeight() <  com.blankj.utilcode.util.ScreenUtils.getScreenHeight()
            // 显示的矩形
            val visibleRect = Rect().also { rect -> it.decorView.getWindowVisibleDisplayFrame(rect) }
            // 底部导航栏占用高度
            val resources = it.context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            val size =  if (resourceId != 0) resources.getDimensionPixelSize(resourceId) else 0
            val navBarHeight = if (navBarShown) size else 0
            // 容器高度 - 显示的底部位置 - 减去底部导航栏的高度
            abs(it.decorView.height - visibleRect.bottom) - navBarHeight
        } ?: 0
    }

    /**
     * 获取[Activity]中[android.R.id.content]未实现高度
     * @param activity [Activity]
     */
    private fun getContentViewInvisibleHeight(activity: Activity?): Int {
        return activity?.window?.let {
            val contentView = it.findViewById<View>(android.R.id.content) ?: return 0
            val outRect = Rect().also { rect -> it.decorView.getWindowVisibleDisplayFrame(rect) }
            abs(contentView.bottom - outRect.height())
        } ?: 0
    }
}