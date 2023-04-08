package cn.cqray.android.graphics

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Contexts
import com.blankj.utilcode.util.ImageUtils

class DrawablePlus : java.io.Serializable {

    /** 颜色值 **/
    private var color: Int? = null

    /** 字节数据 **/
    private var bytes: ByteArray? = null

    fun get(): Drawable? {
        return if (bytes != null) ImageUtils.bytes2Drawable(bytes)
        else if (color != null) ColorDrawable(color!!)
        else null
    }

    fun set(drawable: Drawable?) {
        if (drawable is ColorDrawable) {
            color = drawable.color
            bytes = null
        } else bytes = ImageUtils.drawable2Bytes(drawable)
    }

    fun set(bitmap: Bitmap?) = run { bytes = ImageUtils.bitmap2Bytes(bitmap) }

    @JvmOverloads
    fun set(any: Int, forceColor: Boolean = false) {
        if (forceColor) {
            color = any
            bytes = null
        } else if (Colors.isColorRes(any)) {
            color = Colors.get(any)
            bytes = null
        } else runCatching {
            // 尝试用ID的方式获取背景
            bytes = ImageUtils.drawable2Bytes(ContextCompat.getDrawable(Contexts.get(), any))
        }.onFailure {
            // 失败了则当做颜色处理
            color = any
            bytes = null
        }
    }
}