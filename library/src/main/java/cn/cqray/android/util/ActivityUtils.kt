package cn.cqray.android.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.Get
import cn.cqray.android.Get.context
import cn.cqray.android.app.GetManager
import cn.cqray.android.app.GetNavProvider
import cn.cqray.android.lifecycle.GetActivityLifecycleCallbacks
import cn.cqray.android.lifecycle.GetAppLifecycleCallbacks
import cn.cqray.android.lifecycle.GetFragmentLifecycleCallbacks
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * [Activity]工具类
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ActivityUtils {

    /** Activity列表 **/
    private val activityList = LinkedList<Activity>()

    /** 顶部Activity **/
    private val topActivity = AtomicReference<Activity?>()

    /** 应用实例 **/
    private val application = AtomicReference<Application?>()

    /** App生命周期回调 **/
    private val appLifecycleCallbacks = ArrayList<GetAppLifecycleCallbacks>()


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
     * 获取顶部[Activity]
     */
    fun getTopActivity(): Activity? {
        for (activity in activityList) {
            if (!isActivityAlive(activity)) continue
            return activity
        }
        return null
    }


    fun toActivity(intent: Intent) = toActivity(intent, null as Bundle?)

    /**
     * 跳转指定Activity
     * @param intent 目标意图
     * @param options 配置参数
     */
    fun toActivity(intent: Intent, options: Bundle?): Boolean {
        // 获取上下文
        val context = GetManager.topActivity ?: Get.application
        // 判断目标Activity是否可用
        if (!isIntentAvailable(intent)) {
            return false
        }
        // 不是Activity，需要添加FLAG_ACTIVITY_NEW_TASK标识
        if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 启动Activity
        context.startActivity(intent, options)
        return true
    }

    @JvmStatic
    fun backToActivity(backTo: Class<out Activity>, inclusive: Boolean) {

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun isIntentAvailable(intent: Intent): Boolean {
        return Get.application
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size > 0
    }

//    /**
//     * Return the activity by context.
//     *
//     * @param context The context.
//     * @return the activity by context.
//     */
//    fun getActivityByContext(context: Context?): Activity? {
//        if (context == null) {
//            return null
//        }
//        val activity = getActivityByContextInner(context)
//        return if (!isActivityAlive(activity)) {
//            null
//        } else activity
//    }


//    private fun getOptionsBundle(fragment: Fragment, enterAnim: Int, exitAnim: Int): Bundle? {
//        val activity = fragment.activity ?: return null
//        return ActivityOptionsCompat.makeCustomAnimation(activity, enterAnim, exitAnim).toBundle()
//    }
//
//    private fun getOptionsBundle(
//        context: Context,
//        enterAnim: Int,
//        exitAnim: Int
//    ): Bundle? {
//        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle()
//    }
//
//    private fun getOptionsBundle(
//        fragment: Fragment,
//        sharedElements: Array<View>
//    ): Bundle? {
//        val activity = fragment.activity ?: return null
//        return getOptionsBundle(activity, sharedElements)
//    }
//
//    private fun getOptionsBundle(activity: Activity, sharedElements: Array<View>?): Bundle? {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
//        if (sharedElements == null) return null
//        val len = sharedElements.size
//        if (len <= 0) return null
//        val pairs: Array<Pair<View, String>> = arrayOfNulls<Pair<*, *>>(len)
//        for (i in 0 until len) {
//            pairs[i] = Pair.create(sharedElements[i], sharedElements[i].transitionName)
//        }
//        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs).toBundle()
//    }


    fun context2Activity(context: Context): Activity? {
        var temp = context
        while (temp is ContextWrapper) {
            if (temp is Activity) return temp
            temp = temp.baseContext
        }
        return null
    }

//    private fun getActivityByContextInner(context: Context?): Activity? {
//        var context: Context? = context ?: return null
//        val list: MutableList<Context> = ArrayList()
//        while (context is ContextWrapper) {
//            if (context is Activity) {
//                return context
//            }
//            val activity = getActivityFromDecorContext(context)
//            if (activity != null) {
//                return activity
//            }
//            list.add(context)
//            context = context.baseContext
//            if (context == null) {
//                return null
//            }
//            if (list.contains(context)) {
//                // loop context
//                return null
//            }
//        }
//        return null
//    }
//
//    private fun getActivityFromDecorContext(context: Context?): Activity? {
//        if (context == null) {
//            return null
//        }
//        if (context.javaClass.name == "com.android.internal.policy.DecorContext") {
//            try {
//                val mActivityContextField = context.javaClass.getDeclaredField("mActivityContext")
//                mActivityContextField.isAccessible = true
//                return (mActivityContextField[context] as WeakReference<Activity?>).get()
//            } catch (ignore: Exception) {
//            }
//        }
//        return null
//    }

//    /**
//     * Return whether the activity is alive.
//     *
//     * @param context The context.
//     * @return `true`: yes<br></br>`false`: no
//     */
//    fun isActivityAlive(context: Context?): Boolean {
//        return isActivityAlive(getActivityByContext(context))
//    }

    fun isActivityAlive(activity: Activity) = !activity.isFinishing && !activity.isDestroyed

    /**
     * 检查屏幕横竖屏或者锁定就是固定
     */
    fun isTranslucentOrFloating(activity: Activity): Boolean {
        var isTranslucentOrFloating = false
        try {
            @SuppressLint("PrivateApi") val styleableClass = Class.forName("com.android.internal.R\$styleable")
            val windowField = styleableClass.getDeclaredField("Window")
            windowField.isAccessible = true
            val styleableRes = (windowField[null] as IntArray)
            val typedArray = activity.obtainStyledAttributes(styleableRes)
            val activityInfoClass: Class<*> = ActivityInfo::class.java
            // 调用检查是否屏幕旋转
            @SuppressLint("DiscouragedPrivateApi") val isTranslucentOrFloatingMethod =
                activityInfoClass.getDeclaredMethod("isTranslucentOrFloating", TypedArray::class.java)
            isTranslucentOrFloatingMethod.isAccessible = true
            isTranslucentOrFloating = isTranslucentOrFloatingMethod.invoke(null, typedArray) as Boolean
        } catch (ignored: Exception) {
        }
        return java.lang.Boolean.valueOf(true) == isTranslucentOrFloating
    }

    /**
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     * 修复android 8.0存在的问题
     * 在Activity中onCreate()中super之前调用
     */
    fun hookOrientation(activity: Activity) {
        // 目标版本8.0及其以上
        if (activity.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O) {
            if (isTranslucentOrFloating(activity)) {
                fixOrientation(activity)
            }
        }
    }

    /**
     * 设置屏幕不固定，绕过检查
     */
    private fun fixOrientation(activity: Activity) {
        try {
            val activityClass = Activity::class.java
            val mActivityInfoField = activityClass.getDeclaredField("mActivityInfo")
            mActivityInfoField.isAccessible = true
            val activityInfo = (mActivityInfoField[activity] as ActivityInfo)
            activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } catch (ignored: Exception) {
        }
    }
    //    static void autoHideKeyboard(Object target) {
    //        if (target instanceof ViewProvider) {
    //            boolean autoHide = ((ViewProvider) target).onKeyboardAutoHide();
    //            if (autoHide) {
    //                View view;
    //                if (target instanceof Activity) {
    //                    view = ((Activity) target).findViewById(android.R.id.content);
    //                } else {
    //                    view = ((Fragment) target).requireView();
    //                }
    //                KeyboardUtils.hideSoftInput(view);
    //                View focusView = view.findFocus();
    //                if (focusView != null) {
    //                    focusView.clearFocus();
    //                }
    //            }
    //        }
    //    }
    //    public static void onDestroyed(FragmentActivity activity, Function0<FragmentActivity> function) {
    //        activity.getLifecycle().addObserver();
    //    }


//    private val topActivityOrApp: Context
//        private get() = if (isAppForeground) {
//            val topActivity = topActivity
//            topActivity ?: context
//        } else {
//            context
//        }


    fun getActivityIcon(activity: Activity): Drawable? = getActivityIcon(activity.componentName)

    fun getActivityIcon(clz: Class<out Activity?>): Drawable? = getActivityIcon(ComponentName(context, clz))

    fun getActivityIcon(activityName: ComponentName): Drawable? {
        val pm = Get.application.packageManager
        return try {
            pm.getActivityIcon(activityName)
        } catch (e: NameNotFoundException) {
            null
        }
    }

    fun getActivityLogo(activity: Activity): Drawable? = getActivityLogo(activity.componentName)

    fun getActivityLogo(clz: Class<out Activity?>): Drawable? = getActivityLogo(ComponentName(context, clz))

    fun getActivityLogo(activityName: ComponentName): Drawable? {
        val pm = Get.application.packageManager
        return try {
            pm.getActivityLogo(activityName)
        } catch (e: NameNotFoundException) {
            null
        }
    }
}