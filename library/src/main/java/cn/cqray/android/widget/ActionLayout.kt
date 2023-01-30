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
import cn.cqray.android.util.SizeUnit
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.ViewUtils

/**
 * Action布局控件
 * @author Cqray
 */
@Suppress("unchecked_cast", "unused", "MemberVisibilityCanBePrivate")
class ActionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /** 左间隔  */
    private val startSpace: Space by lazy { Space(context) }

    /** 右间隔  */
    private val endSpace: Space by lazy { Space(context) }

    /** 默认参数 **/
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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ActionLayout)
        // 默认是否启用水波纹
        defaults[ACTION_RIPPLE] = ta.getBoolean(R.styleable.ActionLayout_defaultActionRipple, defaultActionRipple)
        // 默认是否显示Action组件
        defaults[ACTION_VISIBLE] = ta.getBoolean(R.styleable.ActionLayout_defaultActionVisible, defaultActionVisible)
        // 默认组件间隔
        defaults[ACTION_SPACE] = ta.getDimension(R.styleable.ActionLayout_defaultActionSpace, defaultActionSpace)
        // 默认文本颜色
        defaults[ACTION_TEXT_COLOR] =
            ta.getColor(R.styleable.ActionLayout_defaultActionTextColor, defaultActionTextColor)
        // 默认文本大小
        defaults[ACTION_TEXT_SIZE] =
            ta.getDimension(R.styleable.ActionLayout_defaultActionTextSize, defaultActionTextSize)
        // 默认文本样式
        defaults[ACTION_TEXT_STYLE] = ta.getInt(R.styleable.ActionLayout_defaultActionTextStyle, defaultActionTextStyle)
        // 释放资源
        ta.recycle()
        // 天剑间隔容器
        addView(startSpace)
        addView(endSpace)
        // 设置间隔信息
        setActionSpace(defaultActionSpace, SizeUnit.PX)
    }

    /** 默认是否显示水波纹 **/
    private val defaultActionRipple get() = defaults[ACTION_RIPPLE] as Boolean

    /** 默认是否显示组件 **/
    private val defaultActionVisible get() = defaults[ACTION_VISIBLE] as Boolean

    /** 默认组件间隔 **/
    private val defaultActionSpace get() = defaults[ACTION_SPACE] as Float

    /** 默认组件文本颜色 **/
    private val defaultActionTextColor get() = defaults[ACTION_TEXT_COLOR] as Int

    /** 默认组件文本大小 **/
    private val defaultActionTextSize get() = defaults[ACTION_TEXT_SIZE] as Float

    /** 默认组件文本样式 **/
    private val defaultActionTextStyle get() = defaults[ACTION_TEXT_STYLE] as Int

    /** 默认组件图标TintColor **/
    private val defaultActionTintColor get() = defaults[ACTION_ICON_TINT_COLOR] as Int

    fun setActionRipple(ripple: Boolean?) = also {
        (ripple ?: defaultActionRipple).let {
            // 更新组件属性
            actionViews.forEach { entry -> ViewUtils.setRippleBackground(entry.value, it) }
            // 设置默认属性
            defaults[ACTION_RIPPLE] = it
        }
    }

    fun setActionRipple(key: Int?, ripple: Boolean?) = also {
        // 获取新的属性
        val newRipple = ripple ?: actionRipple[key] ?: defaultActionRipple
        // 缓存数据
        actionRipple[key] = newRipple
        // 更改对应控件属性
        actionViews[key]?.let { ViewUtils.setRippleBackground(it, newRipple) }
    }

    fun setActionVisible(visible: Boolean?) = also {
        (visible ?: defaultActionVisible).let {
            // 更新组件属性
            actionViews.forEach { entry -> entry.value.visibility = if (it) VISIBLE else GONE }
            // 设置默认属性
            defaults[ACTION_VISIBLE] = it
        }
    }

    fun setActionVisible(key: Int?, visible: Boolean?) = also {
        // 获取新的属性
        val newVisible = visible ?: actionVisible[key] ?: defaultActionVisible
        // 缓存数据
        actionVisible[key] = newVisible
        // 更改对应控件属性
        actionViews[key]?.let { it.visibility = if (newVisible) VISIBLE else GONE }
    }

    fun setActionSpace(space: Float?) = also { setActionSpace(space, SizeUnit.DIP) }

    fun setActionSpace(space: Float?, unit: SizeUnit) = also {
        // 获取新的间隔值
        val newSpace =
            if (space == null) defaultActionSpace
            else Sizes.applyDimension(space, unit)
        // 获取横纵向间隔值
        val hSize = (if (orientation == HORIZONTAL) newSpace / 2 else 0).toInt()
        val vSize = (if (orientation == VERTICAL) newSpace / 2 else 0).toInt()
        // 更新所有组件的外部间隔
        (startSpace.layoutParams as MarginLayoutParams).setMargins(hSize, vSize, hSize, vSize)
        (endSpace.layoutParams as MarginLayoutParams).setMargins(hSize, vSize, hSize, vSize)
        actionViews.forEach {
            val view = it.value
            (view.layoutParams as MarginLayoutParams).setMargins(hSize, vSize, hSize, vSize)
        }
        // 设置默认属性
        defaults[ACTION_SPACE] = newSpace
    }

    fun setActionView(key: Int?, view: View) = also {
        // 获取相关属性
        val old = actionViews[key]
        val visible = actionVisible[key] ?: defaultActionVisible
        val space = defaultActionSpace.toInt() / 2
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
            setPadding(space, 0, space, 0)
            isClickable = true
            isFocusable = true
            visibility = if (visible) VISIBLE else GONE
            layoutParams = MarginLayoutParams(
                if (orientation == HORIZONTAL) -2 else -1,
                if (orientation == VERTICAL) -2 else -1
            )
        }
        // 缓存控件
        actionViews[key] = view
        // 设置水波纹背景
        ViewUtils.setRippleBackground(view, defaultActionRipple)
        // 添加至容器
        addView(view, index)
    }

    fun setActionText(key: Int?, @StringRes resId: Int?) = also {
        // 无资源ID
        if (resId == null) setActionText(key, null as CharSequence?)
        // 有资源ID
        else setActionText(key, resources.getString(resId))
    }

    fun setActionText(key: Int?, text: CharSequence?) = also {
        // 初始化文本属性
        val tv = AppCompatTextView(context)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.typeface = Typeface.defaultFromStyle(defaultActionTextStyle)
        tv.setTextColor(defaultActionTextColor)
        tv.setTextSize(SizeUnit.PX.type, defaultActionTextSize)
        // 设置ActionView
        setActionView(key, tv)
    }

    fun setActionTextColor(@ColorInt color: Int?) = also {
        (color ?: defaultActionTextColor).let {
            // 更新组件属性
            actionViews.forEach { entry ->
                val view = entry.value
                if (view is TextView) view.setTextColor(it)
            }
            // 设置默认属性
            defaults[ACTION_TEXT_COLOR] = it
        }
    }

    fun setActionTextColor(key: Int?, @ColorInt color: Int?) = also {
        actionViews[key]?.let {
            if (it is TextView) {
                it.setTextColor(color ?: defaultActionTextColor)
            }
        }
    }

    fun setActionTextSize(size: Float?) = also { setActionTextSize(size, SizeUnit.SP) }

    fun setActionTextSize(size: Float?, unit: SizeUnit) {
        // 获取新的文本大小
        val newSize =
            if (size == null) defaultActionTextSize
            else Sizes.applyDimension(size, unit)
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.setTextSize(SizeUnit.PX.type, newSize)
        }
        // 设置默认属性
        defaults[ACTION_TEXT_SIZE] = newSize
    }

    fun setActionTextSize(key: Int?, size: Float?) = also { setActionTextSize(key, size, SizeUnit.SP) }

    fun setActionTextSize(key: Int?, size: Float?, unit: SizeUnit) = also {
        // 获取新的文本大小
        val newSize =
            if (size == null) defaultActionTextSize
            else Sizes.applyDimension(size, unit)
        // 设置新的文本大小
        actionViews[key]?.let {
            if (it is TextView) {
                it.setTextSize(SizeUnit.PX.type, newSize)
            }
        }
    }

    fun setActionTextTypeface(typeface: Typeface?) {
        // 获取新的文本样式
        val newTypeface = typeface ?: Typeface.defaultFromStyle(defaultActionTextStyle)
        // 更新组件属性
        actionViews.forEach { entry ->
            val view = entry.value
            if (view is TextView) view.typeface = newTypeface
        }
        // 设置默认属性
        defaults[ACTION_TEXT_STYLE] = newTypeface.style
    }

    fun setActionTextTypeface(key: Int?, typeface: Typeface?) = also {
        actionViews[key]?.let {
            if (it is TextView) {
                it.typeface = typeface
            }
        }
    }

    fun setActionIcon(key: Int?, @DrawableRes resId: Int?) = also { setActionIcon(key, resId, null) }

    fun setActionIcon(key: Int?, @DrawableRes resId: Int?, @ColorInt tint: Int?) = also {
        // 无资源ID
        if (resId == null) setActionIcon(key, null as Drawable?, tint)
        // 有资源ID
        else setActionIcon(key, ContextCompat.getDrawable(context, resId), tint)
    }

    fun setActionIcon(key: Int?, drawable: Drawable?) = also { setActionIcon(key, drawable, null) }

    fun setActionIcon(key: Int?, drawable: Drawable?, @ColorInt tintColor: Int?) = also {
        // 初始化图片控件
        val iv: ImageView = AppCompatImageView(context).also {
            // 设置图片
            it.setImageDrawable(drawable)
            // 设置TintColor
            ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(tintColor ?: defaultActionTintColor))
        }
        // 设置ActionView组件
        setActionView(key, iv)
    }

    fun setActionIconTintColor(@ColorInt color: Int?) {
        (color ?: defaultActionTintColor).let {
            // 更新组件属性
            actionViews.forEach { entry ->
                val view = entry.value
                if (view is ImageView) ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(it))
            }
            // 设置默认属性
            defaults[ACTION_ICON_TINT_COLOR] = it
        }
    }

    fun setActionIconTintColor(key: Int?, @ColorInt color: Int?) = also {
        actionViews[key]?.let {
            if (it is ImageView) {
                // 获取属性
                val newColor = color ?: defaultActionTintColor
                // 更新组件属性
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(newColor))
            }
        }
    }

    fun setActionListener(key: Int?, listener: OnClickListener?) = also {
        actionViews[key]?.setOnClickListener(listener)
    }

    fun <T : View> getActionView(key: Int?) = actionViews[key] as T?

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