package cn.cqray.android.util

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import cn.cqray.android.Get


@Suppress("ResourceAsColor")
object GetCompat {

//    const val INT_ANY = 0
//    const val INT_RES = 1
//    const val INT_COLOR = 2

//    @JvmStatic
//    fun get(): Context {
//        val act = GetManager.topActivity
//        return act ?: Get.context
//    }

    private val context get() = run { Get.topActivity ?: Get.context }

    /**
     * 获取[Drawable]实例
     * @param any [DrawableRes]资源ID或[ColorInt]颜色
     * @param notId 不是资源ID，则转换成[ColorInt]颜色并获取[Drawable]
     */
    @JvmOverloads
    fun getDrawable(
        @DrawableRes @ColorInt any: Int,
        notId: Boolean = false
    ): Drawable? {
        if (notId) return ColorDrawable(any)
        else runCatching { return ContextCompat.getDrawable(context, any) }
        return ColorDrawable(any)
    }

    /**
     * 获取颜色值
     * @param any [ColorRes]资源ID或[ColorInt]颜色
     * @param notId 不是资源ID，则转换成[ColorInt]颜色
     */
    @JvmOverloads
    fun getColor(
        @ColorRes @ColorInt any: Int,
        notId: Boolean = false
    ): Int {
        if (notId) return any
        else runCatching { return ContextCompat.getColor(context, any) }
        return any
    }

    /**
     * 获取尺寸
     * @param any [DimenRes]资源ID或常规Int值
     * @param forceInt 是否则转换为常规Int值
     */
    @JvmOverloads
    fun getSize(
        any: Int,
        forceInt: Boolean = false
    ): Int {
        if (forceInt) return any
        else runCatching { return context.resources.getDimensionPixelSize(any) }
        return Sizes.dp2px(any.toFloat())
    }
}