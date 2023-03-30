package cn.cqray.android.ui.line

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import cn.cqray.android.R
import cn.cqray.android.util.ContextUtils
import com.blankj.utilcode.util.Utils

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

    /** 左图标 **/
    var icon: Drawable? = null

    /** 右图标 **/
    var next: Drawable? = ContextCompat.getDrawable(Utils.getApp(), R.drawable.def_line_next)

    override val itemType: Int get() = ICON

    fun icon(@DrawableRes id: Int) = also { icon = ContextUtils.getDrawable(id) }

    fun icon(drawable: Drawable?) = also { icon = drawable }

    fun next(@DrawableRes id: Int) = also { next = ContextUtils.getDrawable(id) }

    fun next(drawable: Drawable?) = also { next = drawable }
}