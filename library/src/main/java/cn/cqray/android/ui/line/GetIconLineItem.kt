package cn.cqray.android.ui.line

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import cn.cqray.android.R
import cn.cqray.android.util.ContextUtils
import com.blankj.utilcode.util.ImageUtils

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
    private var iconBytes: ByteArray? = null

    /** 右图标字节数据 **/
    private var nextBytes: ByteArray? = ImageUtils.drawable2Bytes(ContextUtils.getDrawable(R.drawable.def_line_next))

    /** 左图标 **/
    val icon: Drawable? get() = ImageUtils.bytes2Drawable(iconBytes)

    /** 右图标 **/
    val next: Drawable? get() = ImageUtils.bytes2Drawable(nextBytes)

    override val itemType: Int get() = ICON

    fun icon(@DrawableRes id: Int) = also { iconBytes = ImageUtils.drawable2Bytes(ContextUtils.getDrawable(id)) }

    fun icon(drawable: Drawable?) = also { iconBytes = ImageUtils.drawable2Bytes(drawable) }

    fun icon(bitmap: Bitmap?) = also { iconBytes = ImageUtils.bitmap2Bytes(bitmap) }

    fun next(@DrawableRes id: Int) = also { nextBytes = ImageUtils.drawable2Bytes(ContextUtils.getDrawable(id)) }

    fun next(drawable: Drawable?) = also { nextBytes = ImageUtils.drawable2Bytes(drawable) }

    fun next(bitmap: Bitmap?) = also { nextBytes = ImageUtils.bitmap2Bytes(bitmap) }
}