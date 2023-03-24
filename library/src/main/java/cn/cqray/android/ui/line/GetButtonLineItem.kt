package cn.cqray.android.ui.line

import android.util.TypedValue
import androidx.annotation.StringRes
import cn.cqray.android.util.Colors
import cn.cqray.android.util.ContextUtils
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
    var textColor = Colors.text()

    /** 文本大小 **/
    var textSize: Float = Sizes.h3().toFloat()

    /** 文本样式 **/
    var textStyle = 0

    fun text(@StringRes id: Int) = also { this.text = ContextUtils.getString(id) } as T

    fun text(text: CharSequence?) = also { this.text = text } as T

    fun textColor(color: Int) = also { textColor = color } as T

    fun textSize(size: Float) = textSize(size, TypedValue.COMPLEX_UNIT_DIP)

    fun textSize(size: Float, unit: Int) = also { textSize = Sizes.applyDimension(size, unit) } as T

    fun textStyle(textStyle: Int) = also { this.textStyle = textStyle } as T
}