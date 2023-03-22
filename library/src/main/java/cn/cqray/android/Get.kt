package cn.cqray.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Message

import cn.cqray.android.app.GetInit
import com.blankj.utilcode.util.ActivityUtils

/**
 * [Get]框架
 * @author Cqray
 */
object Get {

    /** [GetInit]配置 **/
    @JvmStatic
    var init = GetInit()
        private set


    /** 获取顶部[Activity] **/
    @JvmStatic
    val topActivity: Activity? get() = ActivityUtils.getTopActivity()

    /** 获取[Application]实例 **/
    @JvmStatic
    val application get() = _Get.application

    @JvmStatic
    fun init(application: Application) = init(application, null)

    @JvmStatic
    fun init(application: Application, getInit: GetInit?) {
        // 初始化配置
        this.init = getInit ?: this.init
        _Get.init(application)
    }

    @JvmStatic
    val context: Context get() = application.applicationContext

    /**
     * 在UI线程上延时执行程序
     * @param runnable  需要执行的内容
     * @param delayed   延时时长(ms)
     */
    @JvmStatic
    fun runOnUiThreadDelayed(runnable: Runnable, delayed: Int? = null) = _Get.runOnUiThreadDelayed(runnable, delayed)

    @JvmStatic
    fun toActivity(cls: Class<out Activity>) = ActivityUtils.startActivity(cls)

    @JvmStatic
    fun toActivity(intent: Intent) = ActivityUtils.startActivity(intent)

    @JvmStatic
    fun backToActivity(cls: Class<out Activity>, inclusive: Boolean) = ActivityUtils.finishToActivity(cls, inclusive)
}