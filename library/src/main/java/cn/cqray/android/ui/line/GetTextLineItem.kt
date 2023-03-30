package cn.cqray.android.ui.line

import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import cn.cqray.android.util.Colors
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.Sizes

/**
 * 文本行
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
class GetTextLineItem internal constructor(text: CharSequence?) : GetIconLineItem<GetTextLineItem>(text) {

    /** 右端文本 **/
    var endText: CharSequence? = null

    /** 右端文本颜色 **/
    @ColorInt
    var endTextColor = Colors.text()

    /** 右端文本大小 **/
    var endTextSize = Sizes.pxfH3()

    /** 右端文本样式 **/
    var endTextStyle = 0

    /** 右端提示文本 **/
    var endHint: CharSequence? = null

    /** 右端文本颜色 **/
    @ColorInt
    var endHintColor = Colors.hint()

    override val itemType: Int get() = TEXT

    fun endText(@StringRes id: Int) = also { this.endText = ContextUtils.getString(id) }

    fun endText(text: CharSequence?) = also { this.endText = text }

    fun endTextColor(@ColorInt color: Int) = also { endTextColor = color }

    @JvmOverloads
    fun endTextSize(
        size: Number,
        unit: Int = TypedValue.COMPLEX_UNIT_DIP
    ) = also { endTextSize = Sizes.any2px(size, unit).toFloat() }

    fun endTextStyle(style: Int) = also { this.textStyle = style }

    fun endHint(@StringRes id: Int) = also { this.endHint = ContextUtils.getString(id) }

    fun endHint(text: CharSequence?) = also { this.endHint = text }

    fun endHintColor(@ColorInt color: Int) = also { endHintColor = color }
}