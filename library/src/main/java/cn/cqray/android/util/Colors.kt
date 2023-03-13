package cn.cqray.android.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.app.GetManager

object Colors {

    private val context: Context
        get() {
            val context = GetManager.topActivity
            return context ?: Get.application
        }

    fun get(@ColorRes id: Int) = ContextCompat.getColor(context, id)

    fun foreground() = get(R.color.foreground)

    fun background() = get(R.color.background)

    fun divider() = get(R.color.divider)

    fun hint() = get(R.color.hint)

    fun text() = get(R.color.text)

    fun tint() = get(R.color.tint)

    fun primary() = get(R.color.colorPrimary)

    fun primaryDark() = get(R.color.colorPrimaryDark)

    fun accent() = get(R.color.colorAccent)

}