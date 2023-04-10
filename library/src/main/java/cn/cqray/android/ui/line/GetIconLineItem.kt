package cn.cqray.android.ui.line

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import cn.cqray.android.R
import cn.cqray.android.graphics.CompatDrawable

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

    /** 左图标字节数据 **/
    private var _icon: CompatDrawable = CompatDrawable()

    /** 右图标字节数据 **/
    private var _next: CompatDrawable = CompatDrawable().also { it.set(R.drawable.def_line_next) }

    /** 左图标 **/
    val icon: Drawable? get() = _icon.get()

    /** 右图标 **/
    val next: Drawable? get() = _next.get()

    override val itemType: Int get() = ICON

    fun icon(@DrawableRes id: Int) = also { _icon.set(id) }

    fun icon(drawable: Drawable?) = also { _icon.set(drawable) }

    fun icon(bitmap: Bitmap?) = also { _icon.set(bitmap) }

    fun next(@DrawableRes id: Int) = also { _next.set(id) }

    fun next(drawable: Drawable?) = also { _next.set(drawable) }

    fun next(bitmap: Bitmap?) = also { _next.set(bitmap) }
}