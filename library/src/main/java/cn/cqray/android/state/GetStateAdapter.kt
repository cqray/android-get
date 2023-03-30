package cn.cqray.android.state

import android.graphics.drawable.Drawable
import android.util.TypedValue

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import cn.cqray.android.R
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.ViewUtils

/**
 * 状态适配器
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast"
)
open class GetStateAdapter<T : GetStateAdapter<T>>(@LayoutRes private val layoutResId: Int) {

    /** 关联的容器和布局的内容 **/
    private val views = arrayOfNulls<View>(2)

    /** 默认文本、当前文本 **/
    private val texts = arrayOfNulls<String>(2)

    /** 文本颜色 **/
    private var textColor = Colors.text()

    /** 文本大小 **/
    private var textSize = Sizes.pxfBody()

    /** 文本样式 **/
    private var textStyle: Int = 0

    /** 文本控件 **/
    internal var textView: TextView? = null

    /** 背景 **/
    private var background: Any? = R.color.background

    /** 视图 **/
    val view get() = views[1]

    internal fun onAttach(layout: GetStateLayout) {
        views[0] = layout
        views[1] = onCreateView(layout)
        views[1]?.let {
            it.isClickable = true
            it.isFocusable = true
            onViewCreated(it)
        }
    }

    /**
     * 创建内容视图
     * @param parent 父视图
     */
    protected open fun onCreateView(parent: ViewGroup): View = ViewUtils.inflate(layoutResId)

    /**
     * 控件被创建，仅调用一次
     * @param view 被创建的控件
     */
    protected open fun onViewCreated(view: View) {}

    /**
     * 视图发生变化，会多次调用
     */
    protected open fun onViewChanged(view: View) {
        // 设置文本颜色
        textView?.let {
            // 设置文本
            it.text = texts[1]?.ifEmpty { texts[0] } ?: texts[0]
            // 设置文本颜色
            it.setTextColor(textColor)
            // 设置文本大小
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
        // 背景变化
        when (background) {
            is Int -> view.background = ContextCompat.getDrawable(view.context, background as Int)
            is Drawable -> view.background = background as Drawable
            else -> view.background = null
        }
    }

    /**
     * 显示界面
     * @param text 文本内容
     */
    internal fun show(text: String?) = show(true).let { texts[1] = text }

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
                onViewChanged(it)
                parent.addView(it)
                it.bringToFront()
            } else parent.removeView(it)
        }
    }

    /**
     * 设置默认的文本内容
     * @param text 文本内容
     */
    fun setDefaultText(text: String?) = also { texts[0] = text } as T

    /**
     * 设置文本内容
     * @param text 文本内容
     */
    fun setText(text: String?) = also { texts[1] = text } as T

    /**
     * 设置文本颜色
     * @param color 色值
     */
    fun setTextColor(@ColorInt color: Int) = also { textColor = color } as T

    /**
     * 设置文本大小
     * @param size 大小
     */
    fun setTextSize(size: Number) = setTextSize(size, TypedValue.COMPLEX_UNIT_DIP)

    /**
     * 设置文本大小
     * @param size 大小
     * @param unit 尺寸
     */
    fun setTextSize(size: Number, unit: Int) = also { textSize = Sizes.any2sp(size, unit) } as T

    /**
     * 设置文本样式
     * @param style 样式
     */
    fun setTextStyle(style: Int) = also { textStyle = style } as T

    /**
     * 设置背景
     * @param background [Drawable]
     */
    fun setBackground(background: Drawable?) = also { this.background = background } as T

    /**
     * 设置背景颜色
     * @param color [ColorInt]
     */
    fun setBackgroundColor(@ColorInt color: Int) = also { this.background = color } as T

    /**
     * 设置背景资源ID
     * @param id 资源ID
     */
    fun setBackgroundResource(@DrawableRes id: Int) = also { this.background = id } as T
    
}