package cn.cqray.android.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
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
import cn.cqray.android.util.Sizes.applyDimension
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

    /** 图标位置 **/
    private var iconLocation = IconLocation.START

    /** 是否是垂直方向 **/
    private val vertical get() = iconLocation == IconLocation.TOP || iconLocation == IconLocation.BOTTOM

    /** 是否先绘制图标 **/
    private val iconFirst get() = iconLocation == IconLocation.START || iconLocation == IconLocation.END

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
    val spaceView: Space get() = Space(context).also { it.layoutParams = LayoutParams(-2, -2) }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.IconTextView)
        val space = ta.getDimensionPixelSize(
            R.styleable.IconTextView_sViewSpace,
            resources.getDimensionPixelSize(R.dimen.small)
        )
        val text = ta.getString(R.styleable.IconTextView_android_text) ?: ""
        val textColor = ta.getColor(
            R.styleable.IconTextView_android_textColor,
            ContextCompat.getColor(context, R.color.text)
        )
        val textSize = ta.getDimensionPixelSize(
            R.styleable.IconTextView_android_textSize,
            resources.getDimensionPixelSize(R.dimen.body)
        )
        val textStyle = ta.getInt(R.styleable.IconTextView_android_textStyle, 0)
        val useRipple = ta.getBoolean(R.styleable.IconTextView_useRipple, true)
        val drawable = ta.getDrawable(R.styleable.IconTextView_android_src)

        iconLocation = IconLocation.values()[ta.getInt(R.styleable.IconTextView_sIconLocation, 0)]
        ta.recycle()

        // 设置图标属性
        iconView.setImageDrawable(drawable)
        // 设置文本属性
        textView.let {
            it.text = text
            it.setTextColor(textColor)
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            it.typeface = Typeface.defaultFromStyle(textStyle)
        }
        // 设置间隔属性
        spaceView.let {
            it.layoutParams.width = if (vertical) -1 else space
            it.layoutParams.height = if (vertical) space else -1
            it.visibility = if (text.isEmpty()) GONE else VISIBLE
        }
        // 添加相应的控件
        addView(if (iconFirst) iconView else textView)
        addView(spaceView)
        addView(if (iconFirst) textView else iconView)
        // 设置背景
        ViewUtils.setRippleBackground(iconView, useRipple)
        ViewUtils.setRippleBackground(textView, useRipple)
        // 设置点击事件
        setOnClickListener(null)
    }

    fun setIconLocation(location: IconLocation) = also {
        // 获取间隔大小
        val params = spaceView.layoutParams
        val space = if (vertical) params.height else params.width
        // 更新位置
        iconLocation = location
        removeAllViews()
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
        val viewSpace = applyDimension(space ?: 0F, unit).toInt()
        with(spaceView) {
            layoutParams.width = if (vertical) -1 else viewSpace
            layoutParams.height = if (vertical) viewSpace else -1
            requestLayout()
        }
    }

    fun setUseRipple(useRipple: Boolean?) = also {
        ViewUtils.setRippleBackground(iconView, useRipple)
        ViewUtils.setRippleBackground(textView, useRipple)
    }

    fun setIconDrawable(drawable: Drawable?) = also { iconView.setImageDrawable(drawable) }

    fun setIconBitmap(bitmap: Bitmap?) = also { iconView.setImageBitmap(bitmap) }

    fun setIconResource(@DrawableRes resId: Int?) = also {
        if (resId == null) iconView.setImageDrawable(null)
        else iconView.setImageResource(resId)
    }

    fun setIconTintColor(@ColorInt color: Int?) = also {
        if (color == null)
            ImageViewCompat.setImageTintList(iconView, null)
        else
            ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(color))
    }

    fun setIconTintList(tintList: ColorStateList?) = also {
        ImageViewCompat.setImageTintList(iconView, tintList)
    }

    fun setText(@StringRes resId: Int?) = also {
        if (resId == null) textView.text = null
        else textView.setText(resId)
        spaceView.visibility = if (TextUtils.isEmpty(textView.text)) GONE else VISIBLE
    }

    fun setText(text: CharSequence?) = also {
        textView.text = text
        spaceView.visibility = if ((text ?: "").isEmpty()) GONE else VISIBLE
    }

    fun setTextColor(color: Int?) = also { color?.let { textView.setTextColor(it) } }

    fun setTextSize(textSize: Float?) = also { textSize?.let { textView.textSize = it } }

    fun setTextSize(textSize: Float?, unit: SizeUnit) =
        also { textSize?.let { textView.setTextSize(unit.type, it) } }

    fun setTypeface(typeface: Typeface?) = also { textView.typeface = typeface }

    override fun setOnClickListener(l: OnClickListener?) {
        iconView.setOnClickListener(l)
        textView.setOnClickListener(l)
        super.setOnClickListener {
            iconView.isPressed = true
            iconView.performClick()
        }
    }
}
