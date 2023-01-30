package cn.cqray.android.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Space
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
 * 图标文本控件
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class IconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /** 图标控件 **/
    val iconView: AppCompatImageView
        get() = AppCompatImageView(context).also {
            it.layoutParams = LayoutParams(if (vertical) -1 else -2, if (vertical) -2 else -1)
            it.isFocusable = true
            it.isClickable = true
        }

    /** 文本控件 **/
    val textView: AppCompatTextView
        get() = AppCompatTextView(context).also {
            it.layoutParams = LayoutParams(if (vertical) -1 else -2, if (vertical) -2 else -1)
            it.gravity = Gravity.CENTER
            it.ellipsize = TextUtils.TruncateAt.END
        }

    /** 间隔控件  */
    private val spaceView: Space get() = Space(context).also { it.layoutParams = LayoutParams(-2, -2) }

    /** 默认参数，主要是对应值为空时，赋值 **/
    private val defaults: HashMap<Int, Any?> by lazy {
        val map = HashMap<Int, Any?>()
        map[RIPPLE] = true
        map[SPACE] = Sizes.small()
        map[TEXT_COLOR] = ContextCompat.getColor(context, R.color.text)
        map[TEXT_SIZE] = Sizes.body()
        map[TEXT_STYLE] = 0
        map
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.IconTextView)
        val text = ta.getString(R.styleable.IconTextView_text) ?: ""
        val drawable = ta.getDrawable(R.styleable.IconTextView_src)
        val tintColor = ta.getColor(R.styleable.IconTextView_tint, Color.TRANSPARENT)
        val location = IconLocation.values()[ta.getInt(R.styleable.IconTextView_iconLocation, 0)]
        // 初始化默认属性
        defaults[RIPPLE] = ta.getBoolean(R.styleable.IconTextView_useRipple, defaultRipple)
        defaults[SPACE] = ta.getBoolean(R.styleable.IconTextView_viewSpace, defaultRipple)
        defaults[TEXT_COLOR] = ta.getColor(R.styleable.IconTextView_textColor, defaultTextColor)
        defaults[TEXT_SIZE] = ta.getDimension(R.styleable.IconTextView_textSize, defaultTextSize)
        defaults[TEXT_STYLE] = ta.getInt(R.styleable.IconTextView_textStyle, defaultTextStyle)
        // 释放资源
        ta.recycle()
        // 设置图标属性
        iconView.setImageDrawable(drawable)
        ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(tintColor))
        // 设置文本属性
        textView.let {
            it.text = text
            it.setTextColor(defaultTextColor)
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize)
            it.typeface = Typeface.defaultFromStyle(defaultTextStyle)
        }
        // 设置间隔属性
        spaceView.let {
            it.tag = location
            it.layoutParams.width = if (vertical) -1 else defaultSpace
            it.layoutParams.height = if (vertical) defaultSpace else -1
            it.visibility = if (text.isEmpty()) GONE else VISIBLE
        }
        // 添加相应的控件
        addView(if (iconFirst) iconView else textView)
        addView(spaceView)
        addView(if (iconFirst) textView else iconView)
        // 设置背景
        ViewUtils.setRippleBackground(iconView, defaultRipple)
        ViewUtils.setRippleBackground(textView, defaultRipple)
        // 设置点击事件
        setOnClickListener(null)
    }

    /** 是否是垂直方向 **/
    private val vertical get() = (spaceView.tag as IconLocation).let { it == IconLocation.TOP || it == IconLocation.BOTTOM }

    /** 是否先绘制图标 **/
    private val iconFirst get() = (spaceView.tag as IconLocation).let { it == IconLocation.TOP || it == IconLocation.START }

    /** 默认是否显示水波纹 **/
    private val defaultRipple get() = defaults[RIPPLE] as Boolean

    /** 默认间隔 **/
    private val defaultSpace get() = (defaults[SPACE] as Float).toInt()

    /** 默认组件文本颜色 **/
    private val defaultTextColor get() = defaults[TEXT_COLOR] as Int

    /** 默认组件文本大小 **/
    private val defaultTextSize get() = defaults[TEXT_SIZE] as Float

    /** 默认组件文本样式 **/
    private val defaultTextStyle get() = defaults[TEXT_STYLE] as Int

    fun setIconLocation(location: IconLocation) = also {
        // 更新控件信息
        removeAllViews()
        spaceView.tag = location
        // 获取间隔大小
        val params = spaceView.layoutParams
        val space = if (vertical) params.height else params.width
        // 更新间隔控件尺寸
        spaceView.layoutParams.width = if (vertical) -1 else space
        spaceView.layoutParams.height = if (vertical) space else -1
        // 重新添加控件
        addView(if (iconFirst) iconView else textView)
        addView(spaceView)
        addView(if (iconFirst) textView else iconView)
    }

    fun setViewSpace(space: Float?) = also { setViewSpace(space, SizeUnit.DIP) }

    fun setViewSpace(space: Float?, unit: SizeUnit) = also {
        val newSpace =
            if (space == null) defaultSpace
            else Sizes.applyDimension(space, unit)
        with(spaceView) {
            layoutParams.width = if (vertical) -1 else newSpace.toInt()
            layoutParams.height = if (vertical) newSpace.toInt() else -1
            requestLayout()
        }
    }

    fun setRipple(ripple: Boolean?) = also {
        val newRipple = ripple ?: defaultRipple
        ViewUtils.setRippleBackground(iconView, newRipple)
        ViewUtils.setRippleBackground(textView, newRipple)
    }

    fun setIconDrawable(drawable: Drawable?) = also { iconView.setImageDrawable(drawable) }

    fun setIconBitmap(bitmap: Bitmap?) = also { iconView.setImageBitmap(bitmap) }

    fun setIconResource(@DrawableRes resId: Int?) = also {
        // 无ID资源
        if (resId == null) iconView.setImageDrawable(null)
        // 有ID资源
        else iconView.setImageResource(resId)
    }

    fun setIconTintColor(@ColorInt color: Int?) = also {
        // 无色值
        if (color == null) ImageViewCompat.setImageTintList(iconView, null)
        // 有色值
        else ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(color))
    }

    fun setText(@StringRes resId: Int?) = also {
        val newText =
            if (resId == null) null
            else context.getString(resId)
        setText(newText)
    }

    fun setText(text: CharSequence?) = also {
        val newText = text ?: ""
        textView.text = newText
        spaceView.visibility = if (newText.isEmpty()) GONE else VISIBLE
    }

    fun setTextColor(color: Int?) = also {
        val newColor = color ?: defaultTextColor
        textView.setTextColor(newColor)
    }

    fun setTextSize(size: Float?) = also { setTextSize(size, SizeUnit.SP) }

    fun setTextSize(size: Float?, unit: SizeUnit) = also {
        val newSize =
            if (size == null) defaultTextSize
            else Sizes.applyDimension(size, unit)
        textView.setTextSize(SizeUnit.PX.type, newSize)
    }

    fun setTypeface(typeface: Typeface?) = also {
        val newTypeface = typeface ?: Typeface.defaultFromStyle(defaultTextStyle)
        textView.typeface = newTypeface
    }

    override fun setOnClickListener(l: OnClickListener?) {
        iconView.setOnClickListener(l)
        textView.setOnClickListener(l)
        super.setOnClickListener {
            iconView.isPressed = true
            iconView.performClick()
        }
    }

    private companion object {
        const val RIPPLE = 0
        const val SPACE = 1
        const val TEXT_COLOR = 2
        const val TEXT_SIZE = 3
        const val TEXT_STYLE = 4
    }
}
