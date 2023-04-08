package cn.cqray.android.util

import android.R.color
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.Utils


object Contexts {

    /** 上下文 **/
    @JvmStatic
    fun get(): Context = ActivityUtils.getTopActivity() ?: Utils.getApp().applicationContext

    /**
     * 获取[Drawable]实例
     * @param any [ColorInt]或[DrawableRes]
     * @param forceColor 强制指定[any]为[ColorInt]色值
     */
    @JvmStatic
    @JvmOverloads
    fun getDrawable(any: Int, forceColor: Boolean = false): Drawable? {
        if (forceColor) return ColorDrawable(any)
        else if (isColorRes(any)) return ColorDrawable(Colors.get(any))
        else runCatching {
            // 尝试用ID的方式获取背景
            return ContextUtils.getDrawable(any)!!
        }.onFailure {
            // 失败了则当做颜色处理
            return ColorDrawable(any)
        }
        return null
    }

    /**
     * 获取[Drawable]实例，并装换为Bytes数组
     * @param any [ColorInt]或[DrawableRes]
     * @param forceColor 强制指定[any]为[ColorInt]色值
     */
    @JvmStatic
    @JvmOverloads
    fun getDrawableBytes(
        any: Int,
        forceColor: Boolean = false
    ): ByteArray? = ImageUtils.drawable2Bytes(getDrawable(any, forceColor))

    /** 是否是颜色资源ID **/
    fun isColorRes(any: Int): Boolean {
        val resources = get().resources
        println("数据：${resources.getResourceTypeName(any)}")
        return resources.getResourceTypeName(any) == "color"
    }
}