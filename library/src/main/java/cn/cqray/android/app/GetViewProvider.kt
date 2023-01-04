package cn.cqray.android.app

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import cn.cqray.android.app.GetViewDelegate.Companion.get

/**
 * [GetViewDelegate]功能提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetViewProvider {

    /**
     * 获取并初始化[GetViewDelegate]
     */
    val viewDelegate: GetViewDelegate
        get() = get(this)

    /**
     * 确认[setGetContentView]不被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    fun ensureSetGetContentView() = viewDelegate.ensureSetGetContentView()

    /**
     * 确认[setGetContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    fun ensureSetNativeContentView() = viewDelegate.ensureSetNativeContentView()

    fun setGetContentView(view: View) = viewDelegate.setGetContentView(view)

    fun setGetContentView(layoutResId: Int) = viewDelegate.setGetContentView(layoutResId)

    fun setNativeContentView(view: View) = viewDelegate.setNativeContentView(view)

    fun setNativeContentView(layoutResId: Int) = viewDelegate.setNativeContentView(layoutResId)

    fun setHeaderView(layoutResId: Int) {
        viewDelegate.setHeaderView(layoutResId)
    }

    fun setHeaderView(view: View?) {
        viewDelegate.setHeaderView(view)
    }

    fun setFloatingHeaderView(layoutResId: Int) {
        viewDelegate.setHeaderView(layoutResId)
    }

    fun setFloatingHeaderView(view: View?) {
        viewDelegate.setHeaderView(view)
    }

    fun setHeaderFloating(floating: Boolean) {
        viewDelegate.setHeaderFloating(floating)
    }

    fun setFooterView(layoutResId: Int) {
        viewDelegate.setFooterView(layoutResId)
    }

    fun setFooterView(view: View?) {
        viewDelegate.setFooterView(view)
    }

    fun setFloatingFooterView(layoutResId: Int) {
        viewDelegate.setFooterView(layoutResId)
    }

    fun setFloatingFooterView(view: View?) {
        viewDelegate.setFooterView(view)
    }

    fun setFooterFloating(floating: Boolean) {
        viewDelegate.setFooterFloating(floating)
    }

    fun setBackgroundResource(@DrawableRes resId: Int) = viewDelegate.setBackgroundResource(resId)

    fun setBackgroundColor(color: Int) = viewDelegate.setBackgroundColor(color)

    fun setBackground(drawable: Drawable) = viewDelegate.setBackground(drawable)
}