package cn.cqray.android.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.*
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
import cn.cqray.android.util.ViewUtils
import cn.cqray.java.tool.SizeUnit

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

    /** 默认参数，主要是对应值为空时，赋值 **/
    private val defaults: HashMap<Int, Any?> by lazy {
        val map = HashMap<Int, Any?>()
        map[ACTION_RIPPLE] = true
        map[ACTION_VISIBLE] = true
        map[ACTION_SPACE] = Sizes.content()
        map[ACTION_TEXT_COLOR] = ContextCompat.getColor(context, R.color.foreground)
        map[ACTION_TEXT_SIZE] = Sizes.body()
        map[ACTION_TEXT_STYLE] = 0
        map[ACTION_ICON_TINT_COLOR] = Color.TRANSPARENT
        map
    }

    /** 控件缓存 **/
    private val actionViews = HashMap<Int?, View>()

    /** 控件是否显示水波纹缓存 **/
    private val actionRipple = HashMap<Int?, Boolean>()

    /** 控件是否显示缓存 **/
    private val actionVisible = HashMap<Int?, Boolean>()

    init {
        // 初始化属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetActionLayout)
        // 默认是否启用水波纹
        defaults[ACTION_RIPPLE] = ta.getBoolean(R.styleable.GetActionLayout_defaultRipple, defaultRipple)
        // 默认是否显示Action组件
        defaults[ACTION_VISIBLE] = ta.getBoolean(R.styleable.GetActionLayout_defaultVisible, defaultVisible)
        // 默认组件间隔
        defaults[ACTION_SPACE] = ta.getDimension(R.styleable.GetActionLayout_defaultSpace, defaultSpace)
        // 默认文本颜色
        defaults[ACTION_TEXT_COLOR] = ta.getColor(R.styleable.GetActionLayout_defaultTextColor, defaultTextColor)
        // 默认文本大小
        defaults[ACTION_TEXT_SIZE] = ta.getDimension(R.styleable.GetActionLayout_defaultTextSize, defaultTextSize)
        // 默认文本样式
        defaults[ACTION_TEXT_STYLE] = ta.getInt(R.styleable.GetActionLayout_defaultTextStyle, defaultTextStyle)
        // 释放资源
        ta.recycle()
        // 天剑间隔容器
        addView(startSpaceView)
        addView(endSpaceView)
        // 设置间隔信息
        setSpace(defaultSpace, SizeUnit.PX)
    }

    /** 默认是否显示水波纹 **/
    private val defaultRipple get() = defaults[ACTION_RIPPLE] as Boolean

    /** 默认是否显示组件 **/
    private val defaultVisible get() = defaults[ACTION_VISIBLE] as Boolean

    /** 默认组件间隔 **/
    val defaultSpace get() = defaults[ACTION_SPACE] as Float

    /** 默认组件文本颜色 **/
    private val defaultTextColor get() = defaults[ACTION_TEXT_COLOR] as Int

    /** 默认组件文本大小 **/
    private val defaultTextSize get() = defaults[ACTION_TEXT_SIZE] as Float

    /** 默认组件文本样式 **/
    private val defaultTextStyle get() = defaults[ACTION_TEXT_STYLE] as Int

    /** 默认组件图标TintColor **/
    private val defaultTintColor get() = defaults[ACTION_ICON_TINT_COLOR] as Int

    fun setRipple(ripple: Boolean?) = also {
        (ripple ?: defaultRipple).let {
            // 更新组件属性
            actionViews.forEach { entry -> ViewUtils.setRippleBackground(entry.value, it) }
            // 设置默认属性
            defaults[ACTION_RIPPLE] = it
        }
    }

    fun setRipple(key: Int?, ripple: Boolean?) = also {
        // 获取新的属性
        val newRipple = ripple ?: actionRipple[key] ?: defaultRipple
        // 缓存数据
        actionRipple[key] = newRipple
        // 更改对应控件属性
        actionViews[key]?.let { ViewUtils.setRippleBackground(it, newRipple) }
    }

    fun setVisible(visible: Boolean?) = also {
        (visible ?: defaultVisible).let {
            // 更新组件属性
            actionViews.forEach { entry -> entry.value.visibility = if (it) VISIBLE else GONE }
            // 设置默认属性
            defaults[ACTION_VISIBLE] = it
        }
    }

    fun setVisible(key: Int?, visible: Boolean?) = also {
        // 获取新的属性
        val newVisible = visible ?: actionVisible[key] ?: defaultVisible
        // 缓存数据
        actionVisible[key] = newVisible
        // 更改对应控件属性
        actionViews[key]?.let { it.visibility = if (newVisible) VISIBLE else GONE }
    }

    fun setSpace(space: Float?) = also { setSpace(space, SizeUnit.DIP) }

    fun setSpace(space: Float?, unit: SizeUnit) = also {
        // 获取新的间隔值
        val newSpace =
            if (space == null) defaultSpace
            else Sizes.applyDimension(space, unit)
        // 设置默认属性
        defaults[ACTION_SPACE] = newSpace
        // 更新所有控件的间隔
        changeViewMargin(startSpaceView, newSpace / 2)
        changeViewMargin(endSpaceView, newSpace / 2)
        actionViews.forEach {
            val view = it.value
            changeViewMargin(view, defaultSpace)
        }
    }

    fun setView(key: Int?, view: View) = also {
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
        ViewUtils.setRippleBackground(view, defaultRipple)
        // 添加至容器
        addView(view, index)
    }

    fun setText(key: Int?, @StringRes resId: Int?) = also {
        // 无资源ID
        if (resId == null) setText(key, null as CharSequence?)
        // 有资源ID
        else setText(key, resources.getString(resId))
    }

    fun setText(key: Int?, text: CharSequence?) = also {
        // 初始化文本属性
        val tv = AppCompatTextView(context)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.typeface = Typeface.defaultFromStyle(defaultTextStyle)
        tv.setTextColor(defaultTextColor)
        tv.setTextSize(SizeUnit.PX.type, defaultTextSize)
        // 设置ActionView
        setView(key, tv)
    }

    fun setTextColor(@ColorInt color: Int?) = also {
        (color ?: defaultTextColor).let {
            // 更新组件属性
            actionViews.forEach { entry ->
                val view = entry.value
                if (view is TextView) view.setTextColor(it)
            }
            // 设置默认属性
            defaults[ACTION_TEXT_COLOR] = it
        }
    }

    fun setTextColor(key: Int?, @ColorInt color: Int?) = also {
        actionViews[key]?.let {
            if (it is TextView) {
                it.setTextColor(color ?: defaultTextColor)
            }
        }
    }

    fun setTextSize(size: Float?) = also { setTextSize(size, SizeUnit.SP) }

    fun setTextSize(size: Float?, unit: SizeUnit) {
        // 获取新的文本大小
        val newSize =
            if (size == null) defaultTextSize
            else Sizes.applyDimension(size, unit)
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.setTextSize(SizeUnit.PX.type, newSize)
        }
        // 设置默认属性
        defaults[ACTION_TEXT_SIZE] = newSize
    }

    fun setTextSize(key: Int?, size: Float?) = also { setTextSize(key, size, SizeUnit.SP) }

    fun setTextSize(key: Int?, size: Float?, unit: SizeUnit) = also {
        // 获取新的文本大小
        val newSize =
            if (size == null) defaultTextSize
            else Sizes.applyDimension(size, unit)
        // 设置新的文本大小
        actionViews[key]?.let {
            if (it is TextView) {
                it.setTextSize(SizeUnit.PX.type, newSize)
            }
        }
    }

    fun setTextTypeface(typeface: Typeface?) {
        // 获取新的文本样式
        val newTypeface = typeface ?: Typeface.defaultFromStyle(defaultTextStyle)
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.typeface = newTypeface
        }
        // 设置默认属性
        defaults[ACTION_TEXT_STYLE] = newTypeface.style
    }

    fun setTextTypeface(key: Int?, typeface: Typeface?) = also {
        actionViews[key]?.let {
            if (it is TextView) {
                val newTypeface = typeface ?: Typeface.defaultFromStyle(defaultTextStyle)
                it.typeface = newTypeface
            }
        }
    }

    fun setIcon(key: Int?, @DrawableRes resId: Int?) = also { setIcon(key, resId, null) }

    fun setIcon(key: Int?, @DrawableRes resId: Int?, @ColorInt tint: Int?) = also {
        // 无资源ID
        if (resId == null) setIcon(key, null as Drawable?, tint)
        // 有资源ID
        else setIcon(key, ContextCompat.getDrawable(context, resId), tint)
    }

    fun setIcon(key: Int?, drawable: Drawable?) = also { setIcon(key, drawable, null) }

    fun setIcon(key: Int?, drawable: Drawable?, @ColorInt tintColor: Int?) = also {
        // 初始化图片控件
        val iv: ImageView = AppCompatImageView(context).also {
            // 设置图片
            it.setImageDrawable(drawable)
            // 设置TintColor
            ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(tintColor ?: defaultTintColor))
        }
        // 设置ActionView组件
        setView(key, iv)
    }

    fun setIconTintColor(@ColorInt color: Int?) {
        (color ?: defaultTintColor).let {
            // 更新组件属性
            actionViews.forEach { entry ->
                val view = entry.value
                if (view is ImageView) ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(it))
            }
            // 设置默认属性
            defaults[ACTION_ICON_TINT_COLOR] = it
        }
    }

    fun setIconTintColor(key: Int?, @ColorInt color: Int?) = also {
        actionViews[key]?.let {
            if (it is ImageView) {
                // 获取属性
                val newColor = color ?: defaultTintColor
                // 更新组件属性
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(newColor))
            }
        }
    }

    fun setListener(key: Int?, listener: OnClickListener?) = also {
        actionViews[key]?.setOnClickListener(listener)
    }

    fun <T : View> getActionView(key: Int?) = actionViews[key] as T?

    private fun changeViewMargin(view: View, space: Float) {
        // 获取横纵向间隔值
        val hSize = (if (orientation == HORIZONTAL) space / 2 else 0).toInt()
        val vSize = (if (orientation == VERTICAL) space / 2 else 0).toInt()
        val params = view.layoutParams as LayoutParams
        params.setMargins(hSize, vSize, hSize, vSize)
    }

    private companion object {
        const val ACTION_RIPPLE = 0
        const val ACTION_VISIBLE = 1
        const val ACTION_SPACE = 2
        const val ACTION_TEXT_COLOR = 3
        const val ACTION_TEXT_SIZE = 4
        const val ACTION_TEXT_STYLE = 5
        const val ACTION_ICON_TINT_COLOR = 6
    }
}