package cn.cqray.android.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.*
import cn.cqray.android.Get
import cn.cqray.android.Get.context
import cn.cqray.android.app.GetManager

@Suppress("MemberVisibilityCanBePrivate", "Unused")
object ScreenUtils {

    /** 非NULL上下文 **/
    private fun noNullContext(context: Context?) = context ?: GetManager.topActivity ?: Get.context

    /**
     * 获取屏幕信息
     * @param context 上下文
     */
    @JvmStatic
    private fun getDisplay(context: Context?): Display? {
        val tmp = noNullContext(context)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tmp.display
        } else {
            val vm = tmp.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            @Suppress("Deprecation")
            vm?.defaultDisplay
        }
    }

    /** 屏幕像素密度 **/
    @JvmStatic
    val screenDensity: Float
        get() = Resources.getSystem().displayMetrics.density

    /** 返回以每英寸点数表示的屏幕密度 **/
    @JvmStatic
    val screenDensityDpi: Int
        get() = Resources.getSystem().displayMetrics.densityDpi

    /** 返回X维度中屏幕每英寸的精确物理像素 **/
    @JvmStatic
    val screenXDpi: Float
        get() = Resources.getSystem().displayMetrics.xdpi

    /** 返回X维度中屏幕每英寸的精确物理像素 **/
    @JvmStatic
    val screenYDpi: Float
        get() = Resources.getSystem().displayMetrics.ydpi

    /** 屏幕宽度，单位[SizeUnit.PX] **/
    @JvmStatic
    fun getScreenWidth() = getScreenWidth(null)

    /** 屏幕宽度，单位[SizeUnit.PX] **/
    @JvmStatic
    fun getScreenWidth(context: Context?): Int = Point().also { getDisplay(context)?.getRealSize(it) }.x

    /** 屏幕高度，单位[SizeUnit.PX] **/
    @JvmStatic
    fun getScreenHeight() = getScreenHeight(null)

    /** 屏幕高度，单位[SizeUnit.PX] **/
    @JvmStatic
    fun getScreenHeight(context: Context?): Int = Point().also { getDisplay(context)?.getRealSize(it) }.y

    /** 屏幕宽度，单位[SizeUnit.PX] **/
    @JvmStatic
    fun getAppScreenWidth() = getAppScreenWidth(null)

    /** 应用屏幕宽度，单位[SizeUnit.PX] **/
    @JvmStatic
    @Suppress("Deprecation")
    fun getAppScreenWidth(context: Context?): Int = Point().also { getDisplay(context)?.getSize(it) }.x

    /** 应用屏幕高度，单位[SizeUnit.PX] **/
    @JvmStatic
    fun getAppScreenHeight() = getAppScreenHeight(null)

    /** 应用屏幕高度，单位[SizeUnit.PX] **/
    @JvmStatic
    @Suppress("Deprecation")
    fun getAppScreenHeight(context: Context?): Int = Point().also { getDisplay(context)?.getSize(it) }.y

    /** StatusBar高度 **/
    @JvmStatic
    fun getStatusBarHeight() = getStatusBarHeight(null)

    /** StatusBar高度 **/
    @JvmStatic
    fun getStatusBarHeight(context: Context?): Int {
        val resources = noNullContext(context).resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /** NavigationBar高度 **/
    @JvmStatic
    fun getNavBarHeight() = getNavBarHeight(null)

    /** NavigationBar高度 **/
    @JvmStatic
    fun getNavBarHeight(context: Context?): Int {
        val resources = noNullContext(context).resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) resources.getDimensionPixelSize(resourceId) else 0
    }

//    /** 屏幕配置 **/
//    @JvmStatic
//    val configuration
//        get() = context.resources.configuration!!
//
//    /** 屏幕方向 **/
//    @JvmStatic
//    val orientation
//        get() = configuration.orientation


    /**
     * 返回屏幕上视图的X坐标
     */
    @JvmStatic
    fun getViewX(view: View): Int = IntArray(2).also { view.getLocationOnScreen(it) }[0]

    /**
     * 返回屏幕上视图的Y坐标
     */
    @JvmStatic
    fun getViewY(view: View): Int = IntArray(2).also { view.getLocationOnScreen(it) }[1]

    /**
     * 设置横屏
     */
    @JvmStatic
    @SuppressLint("SourceLockedOrientationActivity")
    fun setLandscape(activity: Activity?) =
        activity?.let { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }

    /**
     * 设置竖屏
     */
    @JvmStatic
    @SuppressLint("SourceLockedOrientationActivity")
    fun setPortrait(activity: Activity?) =
        activity?.let { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }

    /**
     * 屏幕是否为横屏
     */
    @JvmStatic
    fun isLandscape(activity: Activity?): Boolean =
        noNullContext(activity).resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    /**
     * 屏幕是否为竖屏
     */
    @JvmStatic
    fun isPortrait(activity: Activity?): Boolean =
        noNullContext(activity).resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    /**
     * 获取屏幕的旋转角度
     * @param activity [Activity]
     */
    @JvmStatic
    fun getScreenRotation(activity: Activity?): Int {
        return getDisplay(activity)?.let {
            when (it.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
        } ?: 0
    }

    /** 屏幕是否锁定 **/
    @JvmStatic
    fun isScreenLock(): Boolean {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return km.isKeyguardLocked
    }

    /** 底部导航栏是否显示 **/
    @JvmStatic
    fun isNavBarShown() = isNavBarShown(null)

    /** 底部导航栏是否显示 **/
    @JvmStatic
    fun isNavBarShown(activity: Activity? = null): Boolean = getAppScreenHeight(activity) < getScreenHeight(activity)

    /** 状态栏是否显示 **/
    @JvmStatic
    fun isStatusBarShown() = isStatusBarShown(null)

    /** 状态栏是否显示 **/
    @JvmStatic
    fun isStatusBarShown(activity: Activity?): Boolean {
        return (activity ?: GetManager.topActivity)?.let {
            val window = it.window ?: return true
            val outRect = Rect().also { rect -> window.decorView.getWindowVisibleDisplayFrame(rect) }
            return outRect.top > 0
        } ?: true
    }
}