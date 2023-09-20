package cn.cqray.android.app2

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import cn.cqray.android.state.GetStateProvider

/**
 * [GetViewDelegate]功能提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetViewProvider : GetStateProvider, GetProvider {

    /**
     * 获取并初始化[GetViewDelegate]
     */
    val viewDelegate: GetViewDelegate get() = GetDelegate.get(this, GetViewProvider::class.java)

    /**
     * 获取状态委托
     */
    override val stateDelegate get() = viewDelegate.stateDelegate

    /** 根控件 */
    val rootView get() = viewDelegate.rootView

    /** 标题布局 */
    val toolbarLayout get() = viewDelegate.toolbar

    /** 标题 **/
    val toolbar get() = viewDelegate.toolbar

    /** 内容布局 **/
    val contentLayout get() = viewDelegate.contentLayout

    /** 内容视图 **/
    val contentView: View get() = viewDelegate.contentView

    /** 头部布局 */
    val headerLayout get() = viewDelegate.headerLayout

    /** 底部布局 */
    val footerLayout get() = viewDelegate.footerLayout

    /** 刷新布局 **/
    val refreshLayout get() = viewDelegate.refreshLayout

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

    /**
     * 设置布局，根据[ensureSetGetContentView]、[ensureSetNativeContentView]确定布局
     * @param id 布局资源ID
     */
    fun setContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    /**
     * 设置布局，根据[ensureSetGetContentView]、[ensureSetNativeContentView]确定布局
     * @param view 布局
     */
    fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    /**
     * 设置Get风格布局
     * @param view 布局
     */
    fun setGetContentView(view: View) = viewDelegate.setGetContentView(view)

    /**
     * 设置Get风格布局
     * @param id 布局资源ID
     */
    fun setGetContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    /**
     * 设置原生布局
     * @param view 布局
     */
    fun setNativeContentView(view: View) = viewDelegate.setNativeContentView(view)

    /**
     * 设置原生布局
     * @param id 布局资源ID
     */
    fun setNativeContentView(@LayoutRes id: Int) = viewDelegate.setNativeContentView(id)

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     */
    fun setHeaderView(@LayoutRes id: Int) = viewDelegate.setHeaderView(id)

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    fun setHeaderView(@LayoutRes id: Int, floating: Boolean?) = viewDelegate.setHeaderView(id, floating)

    /**
     * 设置顶部视图
     * @param view 视图
     */
    fun setHeaderView(view: View?) = viewDelegate.setHeaderView(view)

    /**
     * 设置顶部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    fun setHeaderView(view: View?, floating: Boolean?) = viewDelegate.setHeaderView(view, floating)

    /**
     * 设置底部视图
     * @param id 视图资源ID
     */
    fun setFooterView(@LayoutRes id: Int) = viewDelegate.setFooterView(id)

    /**
     * 设置底部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    fun setFooterView(@LayoutRes id: Int, floating: Boolean?) = viewDelegate.setFooterView(id, floating)

    /**
     * 设置底部视图
     * @param view 视图
     */
    fun setFooterView(view: View?) = viewDelegate.setFooterView(view)

    /**
     * 设置底部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    fun setFooterView(view: View?, floating: Boolean?) = viewDelegate.setFooterView(view, floating)

    /**
     * 设置背景
     * @param id 资源ID
     */
    fun setBackgroundResource(@DrawableRes id: Int) = viewDelegate.setBackgroundResource(id)

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    fun setBackgroundColor(color: Int) = viewDelegate.setBackgroundColor(color)

    /**
     * 设置背景
     * @param drawable 图像
     */
    fun setBackground(drawable: Drawable?) = viewDelegate.setBackground(drawable)

    /**
     * 显示标题
     */
    fun showToolbar() = viewDelegate.showToolbar()

    /**
     * 隐藏标题
     */
    fun hideToolbar() = viewDelegate.hideToolbar()

    /**
     * 查找View
     */
    fun <T : View> findViewById(@IdRes id: Int): T = viewDelegate.findViewById(id)
}