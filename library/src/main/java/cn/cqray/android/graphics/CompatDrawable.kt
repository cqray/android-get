package cn.cqray.android.graphics

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Contexts
import com.blankj.utilcode.util.ImageUtils

/**
 * 兼容的[Drawable]
 * 存储[ColorDrawable]和常规[Drawable]
 * 可被序列化和Gson转换
 * @author Cqray
 */
class CompatDrawable : java.io.Serializable {

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
        bytes = null
        color = any
        // 强制指定为颜色
        if (forceColor) color = any
        // 是颜色资源
        else if (Colors.isColorRes(any)) color = Colors.get(any)
        // 是Drawable资源
        else runCatching {
            // 尝试用ID的方式获取背景
            val drawable = Contexts.getDrawable(any)
            bytes = ImageUtils.drawable2Bytes(drawable)
        }
    }
}