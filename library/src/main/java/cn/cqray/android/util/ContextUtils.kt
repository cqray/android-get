package cn.cqray.android.util

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import cn.cqray.android.Get
import cn.cqray.android.manage.GetActivityManager

/**
 * @author LeiJue
 * @date 2022/5/19
 */
object ContextUtils {

    @JvmStatic
    fun get(): Context {
        val act = GetActivityManager.topActivity
        return act ?: Get.context
    }

    @JvmStatic
    val resources: Resources
        get() = get().resources
    val assets: AssetManager
        get() = get().assets

    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(get(), resId)
    }

    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    @JvmStatic
    fun inflate(@LayoutRes resId: Int): View {
        val act = GetActivityManager.topActivity
        var parent: ViewGroup? = null
        if (act != null) parent = act.findViewById(android.R.id.content)
        return LayoutInflater.from(get()).inflate(resId, parent, false)
    }
}