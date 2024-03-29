package cn.cqray.android.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.core.content.ContextCompat
import cn.cqray.android.Get
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils

/**
 * 上下文工具类
 * @author Cqray
 */
@Suppress("Unused")
object Contexts {

    /** 上下文 **/
    @JvmStatic
    fun get(): Context = ActivityUtils.getTopActivity() ?: Utils.getApp().applicationContext

    @JvmStatic
    val resources: Resources get() = get().resources

    @JvmStatic
    val assets: AssetManager get() = get().assets
    
    val layoutInflater: LayoutInflater get() = LayoutInflater.from(get())

    /**
     * 获取[Drawable]实例
     * @param id 资源ID
     */
    @JvmStatic
    fun getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(get(), id)

    @JvmStatic
    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(get(), id)

    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    @JvmStatic
    fun inflate(@LayoutRes resId: Int): View {
        val act = Get.topActivity
        var parent: ViewGroup? = null
        if (act != null) parent = act.findViewById(android.R.id.content)
        return LayoutInflater.from(get()).inflate(resId, parent, false)
    }

    @JvmStatic
    fun getIdName(@IdRes id: Int?): String {
        return if (id in arrayOf(View.NO_ID, null)) ""
        else resources.getResourceName(id!!)
    }

    fun context2Activity(context: Context): Activity? {
        var tempContext = context
        while (tempContext is ContextWrapper) {
            if (tempContext is Activity) return tempContext
            else tempContext = tempContext.baseContext
        }
        return null
    }
}