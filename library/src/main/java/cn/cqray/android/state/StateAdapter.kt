package cn.cqray.android.state

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import cn.cqray.android.R
import cn.cqray.android.util.ContextUtils

/**
 * 状态适配器
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast"
)
open class StateAdapter<T : StateAdapter<T>>(
    @param:LayoutRes private val layoutResId: Int
) {

    /** 文本内容 **/
    private var text: String? = null

    /** 默认文本内容  */
    private var defaultText: String? = null

    /** 背景 **/
    private var background: Any? = R.color.background

    /** 关联的容器和布局的内容 **/
    private val views = arrayOfNulls<View>(2)

    /** 视图 **/
    val view get() = views[1]

    /** 是否未连接 **/
    val isNotAttached = views[0] == null

    internal fun onAttach(layout: StateLayout) {
        views[0] = layout
        views[1] = ContextUtils.inflate(layoutResId)
        views[1]?.let {
            it.isClickable = true
            it.isFocusable = true
            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
            onViewCreated(it)
        }
    }

    /**
     * 控件被创建，仅调用一次
     * @param view 被创建的控件
     */
    protected open fun onViewCreated(view: View) {}

    /**
     * 文本内容发生了变化
     * @param text 文本内容
     */
    protected open fun onTextChanged(text: String?) {}

    /**
     * 背景发生了变化
     * @param background 背景
     */
    protected open fun onBackgroundChanged(background: Drawable?) = run {  view?.background = background }

    /**
     * 控件变化后的回调，在[onTextChanged]、[onBackgroundChanged]之后调用,
     * 多次调用，每次显示都会调用
     * @param view 内容控件
     */
    protected open fun onPostViewChanged(view: View) {}

    /**
     * 显示界面
     * @param text 文本内容
     */
    internal fun show(text: String?) {
        this.text = text
        show(true)
    }

    /**
     * 隐藏界面
     */
    internal fun hide() = show(false)

    /**
     * 显示、隐藏实现
     */
    private fun show(show: Boolean) {
        view?.let {
            val parent = views[0]!! as ViewGroup
            if (show) {
                onViewChanged()
                parent.addView(it)
                it.bringToFront()
            } else parent.removeView(it)
        }
    }

    /**
     * 控件变化
     */
    private fun onViewChanged() {
        // 视图未设置，不继续处理
        if (view == null) return
        // 文本变化
        onTextChanged(text ?: defaultText)
        // 背景变化
        when (background) {
            is Int -> onBackgroundChanged(ContextUtils.getDrawable(background as Int))
            is Drawable -> onBackgroundChanged(background as Drawable)
            else -> onBackgroundChanged(null)
        }
        onPostViewChanged(view!!)
    }

    /**
     * 设置文本内容
     * @param text 文本内容
     */
    fun setText(text: String?) = also {
        this.text = text
        view?.let { onTextChanged(text) }
    } as T

    /**
     * 设置默认的文本内容
     * @param text 文本内容
     */
    fun setDefaultText(text: String?) = also { this.defaultText = text } as T

    /**
     * 设置背景
     * @param background [Drawable]
     */
    fun setBackground(background: Drawable?) = also {
        this.background = background
        view?.let { onBackgroundChanged(background) }
    } as T

    /**
     * 设置背景资源
     * @param id 资源ID[DrawableRes]
     */
    fun setBackgroundResource(@DrawableRes id: Int) = also { setBackground(ContextUtils.getDrawable(id)) }

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    fun setBackgroundColor(color: Int) = also { setBackground(ColorDrawable(color)) }
}