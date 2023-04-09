package cn.cqray.android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.os.*
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.app.GetNavProvider
import cn.cqray.android.lifecycle.GetActivityLifecycleCallbacks
import cn.cqray.android.lifecycle.GetAppLifecycleCallbacks
import cn.cqray.android.lifecycle.GetFragmentLifecycleCallbacks
import cn.cqray.android.log.GetLog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * [Get]影子实现，不暴露给外部
 * @author Cqray
 */
@Suppress("ClassName")
internal object _Get {

    /** [Application]实例持有 **/
    private val applicationRef = AtomicReference<Application>()

    /** [Application]生命周期回调 **/
    private val appLifecycleCallbacks = mutableListOf<GetAppLifecycleCallbacks>()

    /** [Handler]控制 **/
    private val handler = Handler(Looper.getMainLooper()) {
        val runnable = it.obj as Runnable
        runnable.run()
        true
    }

    /** 获取[Application]实例 **/
    @JvmStatic
    @get:Synchronized
    val application: Application
        get() {
            if (applicationRef.get() == null) {
                throw RuntimeException("Did you forget call Get.init?")
            }
            return applicationRef.get()
        }

    /**
     * 初始化
     * @param application 应用实例
     */
    fun init(application: Application) {
        // 初始化配置
        applicationRef.set(application)
        Utils.init(application)
        registerActivityLifecycleCallbacks()
    }

    /**
     * 在UI线程上延时执行程序
     * @param runnable  需要执行的内容
     * @param delayed   延时时长(ms)
     */
    fun runOnUiThreadDelayed(runnable: Runnable, delayed: Int? = null) {
        val message = Message.obtain()
        message.what = 0
        message.obj = runnable
        handler.sendMessageDelayed(message, (delayed ?: 0).toLong())
    }

    /**
     * 注册[Activity]生命周期
     */
    private fun registerActivityLifecycleCallbacks() {
        // 前后台状态监听
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            // 进入前台
            override fun onForeground(activity: Activity) = _Get.onForeground(activity)

            // 进入后台
            override fun onBackground(activity: Activity) = _Get.onBackground(activity)
        })
        // Activity生命周期监听
        Utils.getApp().registerActivityLifecycleCallbacks(object : GetActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // 应用首次创建
                if (ActivityUtils.getActivityList().isEmpty()) onCreated()

                // 修复Android 8.0 固定竖屏的问题
                fixOrientation(activity)
                // 监管GetNavProvider
                if (activity is GetNavProvider) {
                    // GetNavDelegate调用onCreated()
                    activity.navDelegate.onCreated()
                    GetFragmentLifecycleCallbacks((activity as FragmentActivity))
                }
                // 打印日志
                printActivityStateLog(activity, "onActivityCreated")
            }

            override fun onActivityStarted(activity: Activity) = printActivityStateLog(activity, "onActivityStarted")

            override fun onActivityResumed(activity: Activity) = printActivityStateLog(activity, "onActivityResumed")

            override fun onActivityPaused(activity: Activity) = printActivityStateLog(activity, "onActivityPaused")

            override fun onActivityStopped(activity: Activity) = printActivityStateLog(activity, "onActivityStopped")

            override fun onActivityDestroyed(activity: Activity) {
                // 打印日志
                printActivityStateLog(activity, "onActivityDestroyed")
                // 延时验证，确保该启动的Activity已启动
                runOnUiThreadDelayed({
                    // 应用程序生命周期结束
                    if (ActivityUtils.getActivityList().isEmpty()) onTerminated()
                }, 10)
            }
        })
    }

    /**
     * 打印Activity状态日志
     * @param activity [Activity]
     * @param state 状态信息
     */
    private fun printActivityStateLog(activity: Activity, state: String) {
        // 获取日志初始化配置
        val logInit = Get.init.logInit!!
        // 未启用则不继续
        if (!logInit.activityLifecycleLogEnable) return
        // 打印日志
        GetLog.d(
            Get::class.java, String.format(
                "%s [%d] -> %s", activity.javaClass.name, activity.hashCode(), state
            )
        )
    }

    /**
     * 程序进入前台回调
     * @param activity 当前Activity
     */
    private fun onForeground(activity: Activity) {
        GetLog.d(this, "Application is on foreground.")
        appLifecycleCallbacks.forEach { it.onForeground(activity) }
    }

    /**
     * 程序退到后台回调
     * @param activity 当前Activity
     */
    private fun onBackground(activity: Activity) {
        GetLog.d(this, "Application is on background.")
        appLifecycleCallbacks.forEach { it.onBackground(activity) }
    }

    /**
     * 程序启动回调
     */
    private fun onCreated() {
        GetLog.d(this, "Application is created.")
        appLifecycleCallbacks.forEach { it.onCreated() }
    }

    /**
     * 程序关闭回调
     */
    private fun onTerminated() {
        appLifecycleCallbacks.forEach { it.onTerminated() }
        handler.removeMessages(0)
        GetLog.d(this, "Application is terminated.")
    }

    /**
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     * 修复android 8.0存在的问题
     * 在Activity中onCreate()中super之前调用
     */
    @SuppressLint(
        "DiscouragedPrivateApi", "SoonBlockedPrivateApi", "PrivateApi"
    )
    private fun fixOrientation(activity: Activity) {
        // 目标版本8.0及其以上
        if (activity.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O) {
            if (isTranslucentOrFloating(activity)) {
                runCatching {
                    val activityClass = Activity::class.java
                    val mActivityInfoField = activityClass.getDeclaredField("mActivityInfo")
                    mActivityInfoField.isAccessible = true
                    val activityInfo = (mActivityInfoField[activity] as ActivityInfo)
                    activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        }
    }

    /**
     * 检查[Activity]横竖屏或者锁定就是固定
     */
    @SuppressLint(
        "DiscouragedPrivateApi",
        "SoonBlockedPrivateApi",
        "PrivateApi"
    )
    fun isTranslucentOrFloating(activity: Activity): Boolean {
        var isTranslucentOrFloating = false
        runCatching {
            val styleableClass = Class.forName("com.android.internal.R\$styleable")
            val windowField = styleableClass.getDeclaredField("Window").also { it.isAccessible = true }
            val styleableRes = (windowField[null] as IntArray)
            val typedArray = activity.obtainStyledAttributes(styleableRes)
            val activityInfoClass: Class<*> = ActivityInfo::class.java
            // 调用检查是否屏幕旋转
            val isTranslucentOrFloatingMethod = activityInfoClass.getDeclaredMethod(
                "isTranslucentOrFloating", TypedArray::class.java
            ).also { it.isAccessible = true }
            isTranslucentOrFloating = isTranslucentOrFloatingMethod.invoke(null, typedArray) as Boolean
        }
        return isTranslucentOrFloating
    }
}