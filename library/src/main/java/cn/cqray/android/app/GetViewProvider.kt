package cn.cqray.android.app

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes

/**
 * [GetViewDelegate]功能提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetViewProvider : GetProvider {

    /**
     * 获取并初始化[GetViewDelegate]
     */
    val viewDelegate: GetViewDelegate
        get() = GetDelegate.get(this, GetViewProvider::class.java)

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

    fun setHeaderView(@LayoutRes id: Int?) = viewDelegate.setHeaderView(id)

    fun setHeaderView(@LayoutRes id: Int?, floating: Boolean?) = viewDelegate.setHeaderView(id, floating)

    fun setHeaderView(view: View?) = viewDelegate.setHeaderView(view)

    fun setHeaderView(view: View?, floating: Boolean?) = viewDelegate.setHeaderView(view, floating)

    fun setFooterView(@LayoutRes id: Int?) = viewDelegate.setFooterView(id)

    fun setFooterView(@LayoutRes id: Int?, floating: Boolean?) = viewDelegate.setFooterView(id, floating)

    fun setFooterView(view: View?) = viewDelegate.setFooterView(view)

    fun setFooterView(view: View?, floating: Boolean?) = viewDelegate.setFooterView(view, floating)

    fun setBackgroundResource(@DrawableRes resId: Int) = viewDelegate.setBackgroundResource(resId)

    fun setBackgroundColor(color: Int) = viewDelegate.setBackgroundColor(color)

    fun setBackground(drawable: Drawable) = viewDelegate.setBackground(drawable)

}