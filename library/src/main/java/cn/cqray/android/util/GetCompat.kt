package cn.cqray.android.util

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import cn.cqray.android.Get

@Suppress("ResourceType")
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
     * @param forceColor 是否强制以颜色获取[Drawable]
     */
    @SuppressLint("ResourceType")
    fun getDrawable(
        @DrawableRes @ColorInt any: Int,
        forceColor: Boolean = false
    ): Drawable? {
        if (forceColor) return ColorDrawable(any)
        else runCatching { return ContextCompat.getDrawable(context, any) }
        return ColorDrawable(any)
    }

    /**
     * 获取颜色值
     * @param any [ColorRes]资源ID或[ColorInt]颜色
     * @param forceColor 是否强制以颜色资源获取
     */
    fun getColor(any: Int, forceColor: Boolean = false): Int {
        if (forceColor) return any
        else runCatching { return ContextCompat.getColor(context, any) }
        return any
    }
}