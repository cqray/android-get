package cn.cqray.android.toolbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.*
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import cn.cqray.android.R
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.Views

/**
 * Action布局控件
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused",
)
class GetActionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /** 左间隔  */
    private val startSpaceView: Space by lazy { Space(context) }

    /** 右间隔  */
    private val endSpaceView: Space by lazy { Space(context) }

    /** 控件缓存 **/
    private val actionViews = HashMap<Int, View>()

    /** 控件是否显示水波纹缓存 **/
    private val actionRipple = HashMap<Int, Boolean>()

    /** 控件是否显示缓存 **/
    private val actionVisible = HashMap<Int, Boolean>()

    /** 默认是否显示水波纹 **/
    private var defaultRipple = true

    /** 默认是否显示组件 **/
    private var defaultVisible = true

    /** 默认组件间隔 **/
    var defaultSpace = 0F
        private set

    /** 默认组件文本颜色 **/
    private var defaultTextColor = -1

    /** 默认组件文本大小 **/
    private var defaultTextSize = 0F

    /** 默认组件文本样式 **/
    private var defaultTextStyle = 0

    /** 默认组件图标TintColor **/
    private var defaultTintColor: Int? = null

    init {
        // 初始化属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetActionLayout)
        // 默认是否启用水波纹
        defaultRipple = ta.getBoolean(R.styleable.GetActionLayout_defaultRipple, true)
        // 默认是否显示Action组件
        defaultVisible = ta.getBoolean(R.styleable.GetActionLayout_defaultVisible, true)
        // 默认组件间隔
        defaultSpace = ta.getDimension(R.styleable.GetActionLayout_defaultSpace, Sizes.px(R.dimen.content).toFloat())
        // 默认文本颜色
        defaultTextColor = ta.getColor(R.styleable.GetActionLayout_defaultTextColor, Color.WHITE)
        // 默认文本大小
        defaultTextSize = ta.getDimension(R.styleable.GetActionLayout_defaultTextSize, Sizes.px(R.dimen.body).toFloat())
        // 默认文本样式
        defaultTextStyle = ta.getInt(R.styleable.GetActionLayout_defaultTextStyle, 0)
        // 释放资源
        ta.recycle()
        // 天剑间隔容器
        addView(startSpaceView)
        addView(endSpaceView)
        // 设置间隔信息
        setDefaultSpace(defaultSpace, TypedValue.COMPLEX_UNIT_PX)
    }

    fun setDefaultRipple(ripple: Boolean) = also {
        // 更新组件属性
        actionViews.forEach { e -> Views.setRippleBackground(e.value, ripple) }
        // 设置默认属性
        defaultRipple = ripple
    }

    fun setDefaultVisible(visible: Boolean) = also {
        // 更新组件属性
        actionViews.forEach { e -> e.value.visibility = if (visible) VISIBLE else GONE }
        // 设置默认属性
        defaultVisible = visible
    }
    
    @JvmOverloads
    fun setDefaultSpace(space: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        // 获取新的间隔值
        val newSpace = Sizes.applyDimension(space, unit)
        // 更新所有控件的间隔
        changeViewMargin(startSpaceView, newSpace / 2)
        changeViewMargin(endSpaceView, newSpace / 2)
        actionViews.forEach {
            val view = it.value
            changeViewMargin(view, defaultSpace)
        }
        // 设置默认属性
        defaultSpace = newSpace
    }

    fun setDefaultTextColor(@ColorInt color: Int) = also {
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.setTextColor(color)
        }
        // 设置默认属性
        defaultTextColor = color
    }

    @JvmOverloads
    fun setDefaultTextSize(size: Number, unit: Int = COMPLEX_UNIT_SP) {
        // 获取新的文本大小
        val newSize = Sizes.applyDimension(size, unit)
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
        }
        // 设置默认属性
        defaultTextSize = newSize
    }

    fun setDefaultTextStyle(style: Int) {
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.typeface = Typeface.defaultFromStyle(style)
        }
        // 设置默认属性
        defaultTextStyle = style
    }

    fun setDefaultIconTintColor(@ColorInt color: Int?) {
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is ImageView) {
                if (color == null) ImageViewCompat.setImageTintList(view, null)
                else ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(color))
            }
        }
        // 设置默认属性
        defaultTintColor = color
    }

    fun setRipple(key: Int, ripple: Boolean) = also {
        // 缓存数据
        actionRipple[key] = ripple
        // 更改对应控件属性
        actionViews[key]?.let { Views.setRippleBackground(it, ripple) }
    }

    fun setVisible(key: Int, visible: Boolean) = also {
        // 缓存数据
        actionVisible[key] = visible
        // 更改对应控件属性
        actionViews[key]?.let { it.visibility = if (visible) VISIBLE else GONE }
    }

    fun setView(key: Int, view: View) = also {
        // 获取相关属性
        val old = actionViews[key]
        val visible = actionVisible[key] ?: defaultVisible
        // 获取全新的索引位置
        val index = old.let {
            // 因为左右有间隔控件
            if (old == null) childCount - 1
            else {
                val i = indexOfChild(old)
                removeView(old)
                // 因为左右有间隔控件
                if (i == -1) 1 else i
            }
        }
        // 设置View通用属性
        with(view) {
            isClickable = true
            isFocusable = true
            visibility = if (visible) VISIBLE else GONE
            layoutParams = LayoutParams(
                if (orientation == HORIZONTAL) -2 else -1,
                if (orientation == VERTICAL) -2 else -1
            )
            changeViewMargin(this, defaultSpace)
        }
        // 缓存控件
        actionViews[key] = view
        // 设置水波纹背景
        Views.setRippleBackground(view, defaultRipple)
        // 添加至容器
        addView(view, index)
    }

    fun setText(key: Int, @StringRes id: Int) = setText(key, resources.getString(id))

    fun setText(key: Int, text: CharSequence?) = also {
        // 初始化文本属性
        val tv = AppCompatTextView(context)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.typeface = Typeface.defaultFromStyle(defaultTextStyle)
        tv.setTextColor(defaultTextColor)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize)
        // 设置ActionView
        setView(key, tv)
    }

    fun setTextColor(key: Int, @ColorInt color: Int) = also {
        actionViews[key]?.let {
            if (it is TextView) {
                it.setTextColor(color)
            }
        }
    }

    @JvmOverloads
    fun setTextSize(key: Int, size: Number, unit: Int = COMPLEX_UNIT_SP) = also {
        // 获取新的文本大小
        val newSize = Sizes.applyDimension(size, unit)
        // 设置新的文本大小
        actionViews[key]?.let {
            if (it is TextView) {
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
            }
        }
    }

    fun setTextStyle(key: Int, style: Int) = also {
        actionViews[key]?.let {
            if (it is TextView) {
                it.typeface = Typeface.defaultFromStyle(style)
            }
        }
    }

    @JvmOverloads
    fun setIcon(key: Int, @DrawableRes id: Int, @ColorInt tintColor: Int? = null) = also {
        setIcon(key, ContextCompat.getDrawable(context, id), tintColor)
    }

    @JvmOverloads
    fun setIcon(key: Int, drawable: Drawable?, @ColorInt tintColor: Int? = null) = also {
        // 初始化图片控件
        val iv: ImageView = AppCompatImageView(context).also {
            // 设置图片
            it.setImageDrawable(drawable)
            // 设置TintColor
            if (tintColor == null) ImageViewCompat.setImageTintList(it, null)
            else ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(tintColor))
        }
        // 设置ActionView组件
        setView(key, iv)
    }

    fun setIconTintColor(key: Int, @ColorInt color: Int?) = also {
        actionViews[key]?.let {
            if (it is ImageView) {
                // 获取属性
                val newColor = color ?: defaultTintColor
                // 更新组件属性
                if (newColor == null) ImageViewCompat.setImageTintList(it, null)
                else ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(newColor))
            }
        }
    }

    fun setListener(key: Int, listener: OnClickListener?) = also { actionViews[key]?.setOnClickListener(listener) }

    fun <T : View> getActionView(key: Int) = actionViews[key] as T?

    private fun changeViewMargin(view: View, space: Float) {
        // 获取横纵向间隔值
        val hSize = (if (orientation == HORIZONTAL) space / 2 else 0).toInt()
        val vSize = (if (orientation == VERTICAL) space / 2 else 0).toInt()
        val params = view.layoutParams as LayoutParams
        params.setMargins(hSize, vSize, hSize, vSize)
    }
}