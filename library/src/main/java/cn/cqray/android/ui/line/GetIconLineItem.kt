package cn.cqray.android.ui.line

import androidx.annotation.DrawableRes
import cn.cqray.android.R

/**
 * 带图标行
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class GetIconLineItem<T : GetIconLineItem<T>> internal constructor(
    text: CharSequence? = null
) : GetButtonLineItem<T>(text) {

    var icon: Int? = null

    var next: Int? = R.drawable.def_line_next

    override val itemType: Int get() = ICON

    fun icon(@DrawableRes id: Int?) = also { icon = id }

    fun next(@DrawableRes id: Int?) = also { next = id }


}