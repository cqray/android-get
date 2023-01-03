package cn.cqray.android

import android.app.Activity
import android.app.Application
import android.content.Context

import cn.cqray.android.app2.GetInit
import cn.cqray.android.manage.GetActivityManager

/**
 * [Get]框架
 * @author Cqray
 */
object Get {

    /** [GetInit]配置 **/
    @JvmStatic
    var init = GetInit()
        private set

    /** 全局[Application] **/
    lateinit var application: Application
        private set

    @JvmStatic
    fun init(application: Application) = init(application, null)

    @JvmStatic
    fun init(application: Application, getInit: GetInit?) {
        // 初始化配置
        this.application = application
        this.init = getInit ?: this.init
        // 初始化Get管理器
        GetActivityManager.init(application)

//        GetDelegate.get()
    }

    @JvmStatic
    val context: Context
        get() = application.applicationContext

    @JvmStatic
    val topActivity: Activity?
        get() = GetActivityManager.topActivity
}