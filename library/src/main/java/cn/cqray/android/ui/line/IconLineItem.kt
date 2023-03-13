package cn.cqray.android.ui.line

import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cn.cqray.android.R
import cn.cqray.android.util.Colors
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.Sizes

/**
 * 带图标行
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
abstract class IconLineItem<T : IconLineItem<T>> internal constructor(
    text: CharSequence? = null
) : ButtonLineItem<T>(text) {

    var icon: Int? = null

    var next: Int? = R.drawable.def_line_next

    override val itemType: Int get() = ICON

    fun icon(@DrawableRes id: Int?) = also { icon = id }

    fun next(@DrawableRes id: Int?) = also { next = id }


}