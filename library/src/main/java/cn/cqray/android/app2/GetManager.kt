package cn.cqray.android.app2

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager

import androidx.fragment.app.FragmentActivity
import cn.cqray.android.Get
import cn.cqray.android.app2.GetNavDelegate.Companion.get
import cn.cqray.android.lifecycle.GetActivityLifecycleCallbacks
import cn.cqray.android.lifecycle.GetAppLifecycleCallbacks
import cn.cqray.android.lifecycle.GetFragmentLifecycleCallbacks
import cn.cqray.android.log.GetLog
import cn.cqray.android.util.ActivityUtils
import cn.cqray.android.util.KeyboardUtils
import cn.cqray.android.util.ThreadUtils
import java.util.*

/**
 * [Get]生命周期管理器
 * @author Cqray
 */
object GetManager {

    /** 处于前台的Activity数量  */
    private var foregroundCount = 0

    /** 配置Configuration的Activity数量  */
    private var configurationCount = 0

    /** 是否处于后台  */
    private var isBackground = false

    /** Application实例  */
    private var application: Application? = null

    /** Handler控制 **/
    private val handler = Handler(Looper.getMainLooper())

    /** Activity列表 **/
    private val activityList = LinkedList<Activity>()

    /** App生命周期回调 **/
    private val appLifecycleCallbacks = ArrayList<GetAppLifecycleCallbacks>()

    @JvmStatic
    fun init(application: Application?) {
        if (application == null) return
        this.application = if (this.application == null) application else return
        registerActivityLifecycleCallbacks()
    }

    @JvmStatic
    @Suppress("unused")
    val isAppForeground: Boolean
        get() = foregroundCount <= 0

    @JvmStatic
    @Suppress("unused")
    val topActivity: Activity?
        get() {
            for (activity in activityList) {
                if (!ActivityUtils.isActivityAlive(activity)) continue
                return activity
            }
            return null
            //throw IllegalStateException("Please make sure use top activity after app is created.")
        }

    @JvmStatic
    fun requireActivity(): Activity {
        val activity = topActivity
        if (activity != null) return activity
        // 抛出异常
        throw IllegalStateException("Please make sure use top activity after app is created.")
    }

    /**
     * 添加App生命周期回调
     * @param callbacks 回调[GetAppLifecycleCallbacks]
     */
    @JvmStatic
    @Suppress("unused")
    fun addAppLifecycleCallbacks(callbacks: GetAppLifecycleCallbacks) =
        appLifecycleCallbacks.add(callbacks)

    /**
     * 添加Activity生命周期回调
     * @param callbacks 回调[GetActivityLifecycleCallbacks]
     */
    @JvmStatic
    @Suppress("unused")
    fun addActivityLifecycleCallbacks(callbacks: GetActivityLifecycleCallbacks) =
        application?.registerActivityLifecycleCallbacks(callbacks)

    /** 注册Activity生命周期回调 **/
    private fun registerActivityLifecycleCallbacks() {
        // 注册生命周期回调
        application!!.registerActivityLifecycleCallbacks(object : GetActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activityList.isEmpty()) {
                    // 通知App调用onCreated()
                    notifyAppLifecycleChanged(null, true)
                    // 通知APP处于前台
                    notifyAppLifecycleChanged(activity, true)
                }

//                LanguageUtils.applyLanguage(activity)
//                UtilsActivityLifecycleImpl.setAnimatorsEnabled()

                setTopActivity(activity)
                // 修复Android 8.0 固定竖屏的问题
                ActivityUtils.hookOrientation(activity)
                // 监管GetNavProvider
                if (activity is GetNavProvider) {
                    get((activity as GetNavProvider)).onCreated()
                    GetFragmentLifecycleCallbacks((activity as FragmentActivity))
                }
                // 打印日志
                printActivityStateLog(activity, "onActivityCreated")
            }

            override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
                // 监管GetNavProvider
                if (activity is GetNavProvider) {
                    get((activity as GetNavProvider)).onViewCreated()
                }
                // 打印日志
                printActivityStateLog(activity, "onActivityPostCreated")
            }

            override fun onActivityStarted(activity: Activity) {
                if (!isBackground) {
                    setTopActivity(activity)
                }
                if (configurationCount < 0) {
                    ++configurationCount
                } else {
                    ++foregroundCount
                }
                // 打印日志
                printActivityStateLog(activity, "onActivityStarted")
            }

            override fun onActivityResumed(activity: Activity) {
                setTopActivity(activity)
                if (isBackground) {
                    isBackground = false
                    notifyAppLifecycleChanged(activity, true)
                }
                processHideSoftInputOnActivityDestroy(activity, false)
                // 打印日志
                printActivityStateLog(activity, "onActivityResumed")
            }

            override fun onActivityPaused(activity: Activity) {
                // 打印日志
                printActivityStateLog(activity, "onActivityPaused")
            }

            override fun onActivityStopped(activity: Activity) {
                if (activity.isChangingConfigurations) {
                    --configurationCount
                } else {
                    --foregroundCount
                    if (foregroundCount <= 0) {
                        isBackground = true
                        // 通知App处于后台
                        notifyAppLifecycleChanged(activity, false)
                    }
                }
                processHideSoftInputOnActivityDestroy(activity, true)
                // 打印日志
                printActivityStateLog(activity, "onActivityStopped")
            }

            override fun onActivityDestroyed(activity: Activity) {
                // 打印日志
                printActivityStateLog(activity, "onActivityDestroyed")
                handler.post {
                    // 监管GetNavProvider
                    if (activity is GetNavProvider) {
                        // 在post中，保证GetNavDelegate的资源最后回收
                        get((activity as GetNavProvider)).onDestroyed()
                    }
                    activityList.remove(activity)
                    // 修复软键盘输入泄漏问题
                    KeyboardUtils.fixSoftInputLeaks(activity)
                    // 没有任何Activity，说明APP关闭
                    if (activityList.isEmpty()) {
                        // 通知APP调用onTerminated()
                        Log.e("数据", "打扰了")
                        notifyAppLifecycleChanged(null, false)
                    }
                }
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
        if (logInit.activityLifecycleLogEnable == false) return
        // 打印日志
        GetLog.d(
            this@GetManager,
            String.format(
                "%s [%d] -> %s",
                activity.javaClass.name,
                activity.hashCode(),
                state
            )
        )
    }

    /**
     * 设置栈顶Activity
     * @param activity [Activity]
     */
    private fun setTopActivity(activity: Activity) {
        if (activityList.contains(activity)) {
            if (activityList.first != activity) {
                activityList.remove(activity)
                activityList.addFirst(activity)
            }
        } else {
            activityList.addFirst(activity)
        }
    }

    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    private fun processHideSoftInputOnActivityDestroy(activity: Activity, isSave: Boolean) {
        try {
            if (isSave) {
                val window = activity.window
                val attrs = window.attributes
                val softInputMode = attrs.softInputMode
                window.decorView.setTag(-123, softInputMode)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            } else {
                val tag = activity.window.decorView.getTag(-123) as? Int ?: return

                ThreadUtils.runOnUiThreadDelayed({
                    try {
                        activity.window?.setSoftInputMode(tag)
                    } catch (ignore: Exception) {
                    }
                }, 100)
            }
        } catch (ignore: Exception) {
        }
    }

    /**
     * 通知App生命周期变化
     * @param activity
     * @param isForeground
     */
    private fun notifyAppLifecycleChanged(activity: Activity?, isForeground: Boolean) {
        // 打印日志
        if (activity == null) {
            if (isForeground) GetLog.d(this, "程序启动")
            else GetLog.d(this, "程序关闭")
        } else {
            if (isForeground) GetLog.d(this, "程序进入前台")
            else GetLog.d(this, "程序退到后台")
        }
        // 程序回调
        for (callback in appLifecycleCallbacks) {
            if (activity == null) {
                // App创建
                if (isForeground) callback.onCreated()
                // App回收
                else callback.onTerminated()
            } else {
                // App前台
                if (isForeground) callback.onForeground(activity)
                // App后台
                else callback.onBackground(activity)
            }
        }
    }
}