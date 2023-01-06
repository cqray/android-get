package cn.cqray.android.widget

import android.content.Context
import android.content.res.ColorStateList
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

/**
 * Action布局控件
 * @author Cqray
 */
@Suppress("unchecked_cast", "unused")
class ActionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {
    /** 左间隔  */
    private val startSpace: Space

    /** 右间隔  */
    private val endSpace: Space

    /** 间隔  */
    private var actionSpace: Int

    /** 文字大小  */
    private var actionTextSize: Int

    /** 文字颜色  */
    private var actionTextColor: Int

    /** 文本样式  */
    private val actionTextStyle: Int

    /** 图标颜色  */
    private var actionIconColor: Int? = null

    /** 是否显示水波纹  */
    private var useRipple: Boolean

    /** 控件列表  */
    private val viewArray = SparseArray<View>()

    /** 控件是否显示列表  */
    private val visibleArray = SparseBooleanArray()

    /** 图片控件TintColor  */
    private val tintColorArray = SparseIntArray()

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        // do nothing.
    }

    fun setActionText(key: Int, @StringRes resId: Int): ActionLayout {
        return setActionText(key, resources.getString(resId))
    }

    fun setActionText(key: Int?, text: CharSequence?): ActionLayout {
        if (key == null) return this
        val index = indexOf(key)
        val horizontal = orientation == HORIZONTAL
        val tv: TextView = AppCompatTextView(context)
        tv.text = text
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionTextSize.toFloat())
        tv.setTextColor(actionTextColor)
        tv.gravity = Gravity.CENTER
        tv.setPadding(actionSpace, 0, actionSpace, 0)
        tv.layoutParams = MarginLayoutParams(if (horizontal) -2 else -1, if (horizontal) -1 else -2)
        tv.isClickable = true
        tv.isFocusable = true
        tv.visibility = if (visibleArray[key]) VISIBLE else GONE
        tv.typeface = Typeface.defaultFromStyle(actionTextStyle)
        addView(tv, index)
        ViewUtils.setRippleBackground(tv, useRipple)
        viewArray.put(key, tv)
        return this
    }

    fun setDefaultActionTextColor(@ColorInt color: Int): ActionLayout {
        actionTextColor = color
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            if (view is TextView) {
                view.setText(actionTextColor)
            }
        }
        return this
    }

    fun setActionTextColor(key: Int, @ColorInt color: Int): ActionLayout {
        val view = viewArray[key]
        if (view is TextView) {
            view.setTextColor(color)
        }
        return this
    }

    fun setDefaultActionTextSize(size: Float): ActionLayout {
        return setDefaultActionTextSize(size, TypedValue.COMPLEX_UNIT_SP)
    }

    fun setDefaultActionTextSize(size: Float, unit: Int): ActionLayout {
        actionTextSize = Sizes.applyDimension(size, unit).toInt()
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            if (view is TextView) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionTextSize.toFloat())
            }
        }
        return this
    }

    fun setActionTextSize(key: Int, size: Float): ActionLayout {
        return setActionTextSize(key, size, TypedValue.COMPLEX_UNIT_SP)
    }

    fun setActionTextSize(key: Int, size: Float, unit: Int): ActionLayout {
        val view = viewArray[key]
        if (view is TextView) {
            view.setTextSize(unit, size)
        }
        return this
    }

    fun setDefaultActionTypeface(typeface: Typeface?): ActionLayout {
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            if (view is TextView) {
                view.typeface = typeface
            }
        }
        return this
    }

    fun setActionTypeface(key: Int, typeface: Typeface?): ActionLayout {
        val view = viewArray[key]
        if (view is TextView) {
            view.typeface = typeface
        }
        return this
    }

    fun setActionIcon(key: Int, @DrawableRes resId: Int): ActionLayout {
        return setActionIcon(key, ContextCompat.getDrawable(context, resId))
    }

    fun setActionIcon(key: Int, @DrawableRes resId: Int, @ColorInt tint: Int): ActionLayout {
        return setActionIcon(key, ContextCompat.getDrawable(context, resId), tint)
    }

    fun setActionIcon(key: Int, drawable: Drawable?): ActionLayout {
        return setActionIcon(key, drawable, null)
    }

    fun setActionIcon(key: Int, drawable: Drawable?, @ColorInt tintColor: Int?): ActionLayout {
        val index = indexOf(key)
        val horizontal = orientation == HORIZONTAL
        val iv: ImageView = AppCompatImageView(context)
        iv.setImageDrawable(drawable)
        iv.layoutParams = MarginLayoutParams(if (horizontal) -2 else -1, if (horizontal) -1 else -2)
        iv.isClickable = true
        iv.isFocusable = true
        iv.setPadding(actionSpace, 0, actionSpace, 0)
        iv.visibility = if (visibleArray[key]) VISIBLE else GONE
        addView(iv, index)
        ViewUtils.setRippleBackground(iv, useRipple)
        val tintList = when {
            tintColor != null -> ColorStateList.valueOf(tintColor)
            tintColorArray[key] != 0 -> ColorStateList.valueOf(tintColorArray[key])
            actionIconColor != null -> ColorStateList.valueOf(actionIconColor!!)
            else -> null
        }
        ImageViewCompat.setImageTintList(iv, tintList)
        viewArray.put(key, iv)
        return this
    }

    fun setActionIconColor(@ColorInt color: Int): ActionLayout {
        actionIconColor = color
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            if (view is ImageView) {
                ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(color))
            }
        }
        return this
    }

    fun setActionIconColor(key: Int, @ColorInt color: Int): ActionLayout {
        tintColorArray.put(key, color)
        val view = viewArray[key]
        if (view is ImageView) {
            ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(color))
        }
        return this
    }

    fun setActionVisible(visible: Boolean): ActionLayout {
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            if (view != null) {
                view.visibility = if (visible) VISIBLE else GONE
            }
        }
        return this
    }

    fun setActionVisible(key: Int, visible: Boolean): ActionLayout {
        visibleArray.put(key, visible)
        val view = viewArray[key]
        if (view != null) {
            view.visibility = if (visible) VISIBLE else GONE
        }
        return this
    }

    fun setDefaultUseRipple(useRipple: Boolean): ActionLayout {
        this.useRipple = useRipple
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            ViewUtils.setRippleBackground(view, useRipple)
        }
        return this
    }

    fun setUseRipple(key: Int, useRipple: Boolean): ActionLayout {
        this.useRipple = useRipple
        val view = viewArray[key]
        if (view != null) {
            ViewUtils.setRippleBackground(view, useRipple)
        }
        return this
    }

    fun setActionSpace(space: Float): ActionLayout {
        return setActionSpace(space, TypedValue.COMPLEX_UNIT_DIP)
    }

    fun setActionSpace(space: Float, unit: Int): ActionLayout {
        actionSpace = Sizes.applyDimension(space, unit).toInt()
        val horizontal = orientation == HORIZONTAL
        startSpace.layoutParams.width = if (horizontal) actionSpace else 0
        startSpace.layoutParams.height = if (!horizontal) actionSpace else 0
        endSpace.layoutParams.width = if (horizontal) actionSpace else 0
        endSpace.layoutParams.height = if (!horizontal) actionSpace else 0
        startSpace.requestLayout()
        endSpace.requestLayout()
        for (i in 0 until viewArray.size()) {
            val view = viewArray.valueAt(i)
            view.setPadding(
                if (horizontal) actionSpace else 0,
                if (!horizontal) actionSpace else 0,
                if (horizontal) actionSpace else 0,
                if (!horizontal) actionSpace else 0
            )
        }
        return this
    }

    fun setActionListener(key: Int, listener: OnClickListener?): ActionLayout {
        val view = viewArray[key]
        view?.setOnClickListener(listener)
        return this
    }

    fun <T : View> getActionView(key: Int) = viewArray[key] as T?

    fun getActionSpace() = actionSpace * 2

    /**
     * 获取对应键值的控件索引
     * @param key 键值
     */
    private fun indexOf(key: Int): Int {
        var index = viewArray.size()
        val view = viewArray[key]
        if (view != null) {
            // 对应键值已添加过控件，则移除控件
            index = viewArray.indexOfKey(key)
            viewArray.remove(key)
            removeView(view)
        } else {
            // 未添加做控件，则直接设置为可见
            visibleArray.put(key, true)
        }
        return index + 1
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ActionLayout)
        actionSpace = ta.getDimensionPixelSize(
            R.styleable.ActionLayout_sActionSpace,
            resources.getDimensionPixelSize(R.dimen.content)
        ) / 2
        actionTextSize = ta.getDimensionPixelSize(
            R.styleable.ActionLayout_sActionTextSize,
            resources.getDimensionPixelSize(R.dimen.body)
        )
        actionTextColor = ta.getColor(
            R.styleable.ActionLayout_sActionTextColor,
            ContextCompat.getColor(context, R.color.text)
        )
        actionTextStyle = ta.getInt(R.styleable.ActionLayout_sActionTextStyle, 0)
        useRipple = ta.getBoolean(R.styleable.ActionLayout_sUseRipple, true)
        ta.recycle()
        startSpace = Space(context)
        endSpace = Space(context)
        addView(startSpace)
        addView(endSpace)
        setActionSpace(actionSpace.toFloat(), TypedValue.COMPLEX_UNIT_PX)
    }
}