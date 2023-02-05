package cn.cqray.android.util

import androidx.annotation.ColorRes
import cn.cqray.android.R
import cn.cqray.android.util.ContextUtils.getColor

object Colors {

    @JvmStatic()
    fun get(@ColorRes id: Int) = getColor(id)

    @JvmStatic
    fun primary() = getColor(R.color.colorPrimary)

    @JvmStatic
    fun foreground() = getColor(R.color.foreground)

    @JvmStatic
    fun background() = getColor(R.color.background)

    @JvmStatic
    fun text() = getColor(R.color.text)

    @JvmStatic
    fun hint() = getColor(R.color.hint)

    @JvmStatic
    fun tint() = getColor(R.color.tint)

    @JvmStatic
    fun dark() = getColor(R.color.dark)

    @JvmStatic
    fun disable() = getColor(R.color.disable)
}