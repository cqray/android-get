package cn.cqray.android.ui.line

import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Contexts
import cn.cqray.android.util.Sizes

/**
 * 按钮行
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class GetButtonLineItem<T : GetButtonLineItem<T>> internal constructor(
    var text: CharSequence? = null
) : GetLineItem<GetButtonLineItem<T>>(BUTTON) {

    /** 文本颜色 **/
    @ColorInt
    var textColor: Int = Colors.text()

    /** 文本大小 **/
    var textSize: Number = Sizes.spH3()

    /** 文本样式 **/
    var textStyle: Int = 0

    fun text(@StringRes id: Int) = also { this.text = Contexts.getString(id) } as T

    fun text(text: CharSequence?) = also { this.text = text } as T

    fun textColor(@ColorInt color: Int) = also { textColor = color } as T

    @JvmOverloads
    fun textSize(
        size: Number,
        unit: Int = TypedValue.COMPLEX_UNIT_DIP
    ) = also { textSize = Sizes.any2sp(size, unit) } as T

    fun textStyle(style: Int) = also { this.textStyle = style } as T
}