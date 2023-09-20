package cn.cqray.android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.database.Cursor
import android.net.Uri
import android.os.*
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.app.GetNavProvider
import cn.cqray.android.handle.GetTaskDelegate
import cn.cqray.android.lifecycle.GetActivityLifecycleCallbacks
import cn.cqray.android.lifecycle.GetAppLifecycleCallbacks
import cn.cqray.android.lifecycle.GetFragmentLifecycleCallbacks
import cn.cqray.android.log.GetLog
import cn.cqray.android.tip.GetTip
import cn.cqray.android.util.Check3rdUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*

/**
 * [Get]影子实现，不暴露给外部
 * @author Cqray
 */
@Suppress("ClassName")
internal class _Get : ContentProvider() {

    //========================================================
    //=================ContentProvider相关部分=================
    //========================================================

    override fun onCreate(): Boolean {
        // 初始化Application
        application = context!!.applicationContext as Application
        // 初始化工具类
        Utils.init(application)
        // 初始化提示
        GetTip.init(application)
        // 检查第三方框架是否可用
        Check3rdUtils.check()
        // 注册生命周期管理
        registerActivityLifecycleCallbacks()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0

    //========================================================
    //=====================Get框架初始化部分====================
    //========================================================

    /** [Application]生命周期回调 **/
    private val appLifecycleCallbacks = mutableListOf<GetAppLifecycleCallbacks>()

    /**
     * 注册[Activity]生命周期
     */
    private fun registerActivityLifecycleCallbacks() {
        // 前后台状态监听
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            // 进入前台
            override fun onForeground(activity: Activity) = this@_Get.onForeground(activity)

            // 进入后台
            override fun onBackground(activity: Activity) = this@_Get.onBackground(activity)
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
            }

            override fun onActivityDestroyed(activity: Activity) {
                // 延时验证，确保该启动的Activity已启动
                runOnUiThreadDelayed({
                    // 应用程序生命周期结束
                    if (ActivityUtils.getActivityList().isEmpty()) onTerminated()
                }, 15)
            }
        })
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
        taskDelegate.onCleared()
        GetLog.d(this, "Application is terminated.")
    }

    /**
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     * 修复android 8.0存在的问题
     * 在Activity中onCreate()中super之前调用
     */
    @SuppressLint(
        "DiscouragedPrivateApi",
        "SoonBlockedPrivateApi",
        "PrivateApi"
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

    companion object {
        /** [Application]实例 **/
        lateinit var application: Application
            private set

        /** 任务委托 **/
        private val taskDelegate by lazy { GetTaskDelegate() }

        /**
         * 在UI线程上延时执行程序
         * @param runnable  需要执行的内容
         * @param delayed   延时时长(ms)
         */
        fun runOnUiThreadDelayed(runnable: Runnable, delayed: Int? = null) {
            val delay = delayed ?: 0
            // 在主线程，且延时小于等于0，直接运行
            if (Looper.myLooper() == Looper.getMainLooper() && delay <= 0) {
                runnable.run()
                return
            }
            // 定时任务
            taskDelegate.timerTask({ runnable.run() }, delay)
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
                return isTranslucentOrFloatingMethod.invoke(null, typedArray) as Boolean
            }
            return false
        }
    }
}