package cn.cqray.android.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import cn.cqray.android.R

object Colors {

    private val context: Context get() = ContextUtils.get()

    @JvmStatic
    fun get(@ColorRes id: Int) = ContextCompat.getColor(context, id)

    @JvmStatic
    fun foreground() = get(R.color.foreground)

    @JvmStatic
    fun background() = get(R.color.background)

    @JvmStatic
    fun divider() = get(R.color.divider)

    @JvmStatic
    fun hint() = get(R.color.hint)

    @JvmStatic
    fun text() = get(R.color.text)

    @JvmStatic
    fun tint() = get(R.color.tint)

    @JvmStatic
    fun primary() = get(R.color.colorPrimary)

    @JvmStatic
    fun primaryDark() = get(R.color.colorPrimaryDark)

    @JvmStatic
    fun accent() = get(R.color.colorAccent)

    /** 是否是颜色资源ID **/
    fun isColorRes(any: Int): Boolean {
        val resources = Contexts.get().resources
        return resources.getResourceTypeName(any) == "color"
    }
}