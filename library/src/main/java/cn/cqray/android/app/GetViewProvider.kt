package cn.cqray.android.app

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import cn.cqray.android.state.StateProvider

/**
 * [GetViewDelegate]功能提供者
 * @author Cqray
 */
@Suppress("Deprecation")
@JvmDefaultWithoutCompatibility
interface GetViewProvider : StateProvider, GetProvider {

    /**
     * 获取并初始化[GetViewDelegate]
     */
    val viewDelegate: GetViewDelegate get() = GetDelegate.get(this, GetViewProvider::class.java)

    /**
     * 获取状态委托
     */
    override val stateDelegate get() = viewDelegate.stateDelegate

    /**
     * 确认[setGetContentView]不被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    @JvmDefault
    fun ensureSetGetContentView() = viewDelegate.ensureSetGetContentView()

    /**
     * 确认[setGetContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    @JvmDefault
    fun ensureSetNativeContentView() = viewDelegate.ensureSetNativeContentView()

    /**
     * 设置布局，根据[ensureSetGetContentView]、[ensureSetNativeContentView]确定布局
     * @param id 布局资源ID
     */
    @JvmDefault
    fun setContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    /**
     * 设置布局，根据[ensureSetGetContentView]、[ensureSetNativeContentView]确定布局
     * @param view 布局
     */
    @JvmDefault
    fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    /**
     * 设置Get风格布局
     * @param view 布局
     */
    @JvmDefault
    fun setGetContentView(view: View) = viewDelegate.setGetContentView(view)

    /**
     * 设置Get风格布局
     * @param id 布局资源ID
     */
    @JvmDefault
    fun setGetContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    /**
     * 设置原生布局
     * @param view 布局
     */
    @JvmDefault
    fun setNativeContentView(view: View) = viewDelegate.setNativeContentView(view)

    /**
     * 设置原生布局
     * @param id 布局资源ID
     */
    @JvmDefault
    fun setNativeContentView(@LayoutRes id: Int) = viewDelegate.setNativeContentView(id)

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     */
    @JvmDefault
    fun setHeaderView(@LayoutRes id: Int) = viewDelegate.setHeaderView(id)

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    @JvmDefault
    fun setHeaderView(@LayoutRes id: Int, floating: Boolean?) = viewDelegate.setHeaderView(id, floating)

    /**
     * 设置顶部视图
     * @param view 视图
     */
    @JvmDefault
    fun setHeaderView(view: View?) = viewDelegate.setHeaderView(view)

    /**
     * 设置顶部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    @JvmDefault
    fun setHeaderView(view: View?, floating: Boolean?) = viewDelegate.setHeaderView(view, floating)

    /**
     * 设置底部视图
     * @param id 视图资源ID
     */
    @JvmDefault
    fun setFooterView(@LayoutRes id: Int) = viewDelegate.setFooterView(id)

    /**
     * 设置底部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    @JvmDefault
    fun setFooterView(@LayoutRes id: Int, floating: Boolean?) = viewDelegate.setFooterView(id, floating)

    /**
     * 设置底部视图
     * @param view 视图
     */
    @JvmDefault
    fun setFooterView(view: View?) = viewDelegate.setFooterView(view)

    /**
     * 设置底部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    @JvmDefault
    fun setFooterView(view: View?, floating: Boolean?) = viewDelegate.setFooterView(view, floating)

    /**
     * 设置背景
     * @param id 资源ID
     */
    @JvmDefault
    fun setBackgroundResource(@DrawableRes id: Int) = viewDelegate.setBackgroundResource(id)

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    @JvmDefault
    fun setBackgroundColor(color: Int) = viewDelegate.setBackgroundColor(color)

    /**
     * 设置背景
     * @param drawable 图像
     */
    @JvmDefault
    fun setBackground(drawable: Drawable?) = viewDelegate.setBackground(drawable)

    /**
     * 显示标题
     */
    @JvmDefault
    fun showToolbar() = viewDelegate.showToolbar()

    /**
     * 隐藏标题
     */
    @JvmDefault
    fun hideToolbar() = viewDelegate.hideToolbar()

    /**
     * 侵入标题
     */
    @JvmDefault
    fun immersionToolbar() = viewDelegate.immersionToolbar()

}