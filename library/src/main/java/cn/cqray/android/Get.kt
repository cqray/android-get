 package cn.cqray.android

import android.app.Activity
import android.app.Application
import android.content.Context
import butterknife.Unbinder

import cn.cqray.android.app.GetInit
import cn.cqray.android.app.GetManager
import cn.cqray.android.log.GetLog
import cn.cqray.android.util.ThirdCheckUtils

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
        GetManager.init(application)


//        GetDelegate.get()
    }

    @JvmStatic
    val context: Context
        get() = application.applicationContext

    @JvmStatic
    val topActivity: Activity?
        get() = GetManager.topActivity
}