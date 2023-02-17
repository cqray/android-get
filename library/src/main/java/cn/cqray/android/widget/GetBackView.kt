package cn.cqray.android.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
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
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.ViewUtils
import cn.cqray.java.tool.SizeUnit

/**
 * 图标文本控件
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused",
)
class GetBackView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /** 图标控件 **/
    val iconView: AppCompatImageView by lazy {
        AppCompatImageView(context).also {
            it.layoutParams = LayoutParams(-2, -1)
            it.isFocusable = true
            it.isClickable = true
        }
    }

    /** 文本控件 **/
    val textView: AppCompatTextView by lazy {
        AppCompatTextView(context).also {
            it.layoutParams = LayoutParams(-2, -1)
            it.gravity = Gravity.CENTER
            it.ellipsize = TextUtils.TruncateAt.END
        }
    }

    /** 间隔控件  */
    private val spaceView: Space by lazy { Space(context).also { it.layoutParams = LayoutParams(-2, -2) } }

    /** 默认参数，主要是对应值为空时，赋值 **/
    private val defaults: HashMap<Int, Any?> by lazy {
        val map = HashMap<Int, Any?>()
        map[RIPPLE] = true
        map[ICON_SPACE] = Sizes.small()
        map[TEXT_COLOR] = ContextCompat.getColor(context, R.color.text)
        map[TEXT_SIZE] = Sizes.body()
        map[TEXT_STYLE] = 0
        map
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetBackView)
        val text = ta.getString(R.styleable.GetBackView_text) ?: ""
        val drawable = ta.getDrawable(R.styleable.GetBackView_iconSrc)
        val tintColor = ta.getColor(R.styleable.GetBackView_iconTint, -1)
        // 初始化默认属性
        defaults[RIPPLE] = ta.getBoolean(R.styleable.GetBackView_ripple, defaultRipple)
        defaults[ICON_SPACE] = ta.getDimension(R.styleable.GetBackView_iconSpace, defaultIconSpace)
        defaults[TEXT_COLOR] = ta.getColor(R.styleable.GetBackView_textColor, defaultTextColor)
        defaults[TEXT_SIZE] = ta.getDimension(R.styleable.GetBackView_textSize, defaultTextSize)
        defaults[TEXT_STYLE] = ta.getInt(R.styleable.GetBackView_textStyle, defaultTextStyle)
        // 释放资源
        ta.recycle()
        // 设置图标属性
        iconView.setImageDrawable(drawable)
        ImageViewCompat.setImageTintList(
            iconView, when (tintColor) {
                -1 -> null
                else -> ColorStateList.valueOf(tintColor)
            }
        )
        // 设置文本属性
        textView.let {
            it.text = text
            it.setTextColor(defaultTextColor)
            it.setTextSize(SizeUnit.PX.type, defaultTextSize)
            it.typeface = Typeface.defaultFromStyle(defaultTextStyle)
        }
        // 设置间隔属性
        spaceView.let {
            it.layoutParams.width = defaultIconSpace.toInt()
            it.layoutParams.height = -1
            it.visibility = if (text.isEmpty()) GONE else VISIBLE
        }
        // 判断布局方向
        val direction = resources.configuration.layoutDirection
        val ltr = direction == View.LAYOUT_DIRECTION_LTR
        // 添加相应的控件
        addView(if (ltr) iconView else textView)
        addView(spaceView)
        addView(if (ltr) textView else iconView)
        // 设置背景
        ViewUtils.setRippleBackground(iconView, defaultRipple)
        ViewUtils.setRippleBackground(textView, defaultRipple)
        // 设置点击事件
        setOnClickListener(null)
    }

    /** 默认是否显示水波纹 **/
    private val defaultRipple get() = defaults[RIPPLE] as Boolean

    /** 默认间隔 **/
    private val defaultIconSpace get() = defaults[ICON_SPACE] as Float

    /** 默认组件文本颜色 **/
    private val defaultTextColor get() = defaults[TEXT_COLOR] as Int

    /** 默认组件文本大小 **/
    private val defaultTextSize get() = defaults[TEXT_SIZE] as Float

    /** 默认组件文本样式 **/
    private val defaultTextStyle get() = defaults[TEXT_STYLE] as Int

    fun setRipple(ripple: Boolean?) = also {
        val newRipple = ripple ?: defaultRipple
        ViewUtils.setRippleBackground(iconView, newRipple)
        ViewUtils.setRippleBackground(textView, newRipple)
    }

    fun setIconDrawable(drawable: Drawable?) = also {
        // 更新图片
        iconView.setImageDrawable(drawable)
        // 更新间隔控件状态
        changeSpaceVisibility()
    }

    fun setIconBitmap(bitmap: Bitmap?) = also {
        // 更新图片
        iconView.setImageBitmap(bitmap)
        // 更新间隔控件状态
        changeSpaceVisibility()
    }

    fun setIconResource(@DrawableRes resId: Int?) = also {
        // 无ID资源
        if (resId == null) iconView.setImageDrawable(null)
        // 有ID资源
        else iconView.setImageResource(resId)
        // 更新间隔控件状态
        changeSpaceVisibility()
    }

    fun setIconTintColor(@ColorInt color: Int?) = also {
        // 无色值
        if (color == null) ImageViewCompat.setImageTintList(iconView, null)
        // 有色值
        else ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(color))
    }

    fun setIconSpace(space: Float?) = also { setIconSpace(space, SizeUnit.DIP) }

    fun setIconSpace(space: Float?, unit: SizeUnit) = also {
        val newSpace =
            if (space == null) defaultIconSpace
            else Sizes.applyDimension(space, unit)
        spaceView.layoutParams.width = newSpace.toInt()
        spaceView.requestLayout()
    }

    fun setText(@StringRes resId: Int?) = also {
        setText(
            if (resId == null) null
            else context.getString(resId)
        )
    }

    fun setText(text: CharSequence?) = also {
        // 更新文本
        textView.text = text
        // 更新间隔控件状态
        changeSpaceVisibility()
    }

    fun setTextColor(color: Int?) = also {
        val newColor = color ?: defaultTextColor
        textView.setTextColor(newColor)
    }

    fun setTextSize(size: Float?) = also { setTextSize(size, SizeUnit.SP) }

    fun setTextSize(size: Float?, unit: SizeUnit) = also {
        val newSize = if (size == null) defaultTextSize
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

    /**
     * 改变间隔组件显示状态
     */
    private fun changeSpaceVisibility() {
        if (iconView.drawable == null) {
            // 没有图片，直接不显示间隔控件
            spaceView.visibility = GONE
        } else {
            // 有文字则显示间隔，无则不显示
            spaceView.visibility = if (textView.text.isEmpty()) GONE else VISIBLE
        }
    }

    private companion object {
        const val RIPPLE = 0
        const val ICON_SPACE = 1
        const val TEXT_COLOR = 2
        const val TEXT_SIZE = 3
        const val TEXT_STYLE = 4
    }
}
