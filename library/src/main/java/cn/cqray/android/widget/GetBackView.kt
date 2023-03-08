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
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.ImageViewCompat
import cn.cqray.android.R
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.ViewUtils

/**
 * 图标文本控件
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "UNUSED",
)
class GetBackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
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

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetBackView)
        val text = ta.getString(R.styleable.GetBackView_text) ?: ""
        val drawable = ta.getDrawable(R.styleable.GetBackView_iconSrc)
        val tintColor = ta.getColor(R.styleable.GetBackView_iconTint, -1)
        // 初始化默认属性
        val ripple = ta.getBoolean(R.styleable.GetBackView_ripple, true)
        val iconSpace = ta.getDimension(R.styleable.GetBackView_iconSpace, Sizes.px(R.dimen.small))
        val textColor = ta.getColor(R.styleable.GetBackView_textColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.GetBackView_textSize, Sizes.px(R.dimen.body))
        val textStyle = ta.getInt(R.styleable.GetBackView_textStyle, 0)
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
            it.setTextColor(textColor)
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            it.typeface = Typeface.defaultFromStyle(textStyle)
        }
        // 设置间隔属性
        spaceView.let {
            it.layoutParams.width = iconSpace.toInt()
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
        ViewUtils.setRippleBackground(iconView, ripple)
        ViewUtils.setRippleBackground(textView, ripple)
        // 设置点击事件
        setOnClickListener(null)
    }

    fun setRipple(ripple: Boolean) = also {
        ViewUtils.setRippleBackground(iconView, ripple)
        ViewUtils.setRippleBackground(textView, ripple)
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

    fun setIconResource(@DrawableRes id: Int) = also {
        iconView.setImageResource(id)
        // 更新间隔控件状态
        changeSpaceVisibility()
    }

    fun setIconTintColor(@ColorInt color: Int?) = also {
        // 无色值
        if (color == null) ImageViewCompat.setImageTintList(iconView, null)
        // 有色值
        else ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(color))
    }

    fun setIconSpace(space: Float) = also { setIconSpace(space, TypedValue.COMPLEX_UNIT_DIP) }

    fun setIconSpace(space: Float, unit: Int) = also {
        spaceView.layoutParams.width = Sizes.applyDimension(space, unit).toInt()
        spaceView.requestLayout()
    }

    fun setText(@StringRes id: Int) = setText(context.getString(id))

    fun setText(text: CharSequence?) = also {
        // 更新文本
        textView.text = text
        // 更新间隔控件状态
        changeSpaceVisibility()
    }

    fun setTextColor(color: Int) = also { textView.setTextColor(color) }

    fun setTextSize(size: Float) = also { setTextSize(size, TypedValue.COMPLEX_UNIT_SP) }

    fun setTextSize(size: Float, unit: Int) = also { textView.setTextSize(unit, size) }

    fun setTextStyle(textStyle: Int) = also { textView.typeface = Typeface.defaultFromStyle(textStyle) }

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
}
