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
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import cn.cqray.android.Get
import cn.cqray.android.Get.context
import cn.cqray.android.app.GetManager
import com.blankj.utilcode.util.ScreenUtils

@Suppress("MemberVisibilityCanBePrivate", "Unused")
object ScreenUtils {

    /** 窗口管理器 **/
    val windowManager: WindowManager? = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager

    /** 屏幕显示信息 **/
    val display: Display?
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                GetManager.topActivity?.display
            } else {
                val vm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                @Suppress("Deprecation")
                vm?.defaultDisplay
            }
        }

    /** 屏幕宽度，单位[SizeUnit.PX] **/
    val screenWidth: Int
        get() {
            val point = Point().also { display?.getRealSize(it) }
            return point.x
        }

    /** 屏幕高度，单位[SizeUnit.PX] **/
    val screenHeight: Int
        get() {
            val point = Point().also { display?.getRealSize(it) }
            return point.y
        }

    /** 应用屏幕宽度，单位[SizeUnit.PX] **/
    val appScreenWidth: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val rect = windowManager?.currentWindowMetrics?.bounds ?: Rect()
                rect.width()
            } else {
                @Suppress("Deprecation")
                val point = Point().also { display?.getSize(it) }
                point.x
            }
        }

    /** 应用屏幕高度，单位[SizeUnit.PX] **/
    val appScreenHeight: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val rect = windowManager?.currentWindowMetrics?.bounds ?: Rect()
                rect.height()
            } else {
                @Suppress("Deprecation")
                val point = Point().also { display?.getSize(it) }
                point.y
            }
        }

    /** StatusBar高度 **/
    val statusBarHeight: Int
        get() {
            val resources = context.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

    /** NavigationBar高度 **/
    val navBarHeight: Int
        get() {
            val res = context.resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId != 0) res.getDimensionPixelSize(resourceId) else 0
        }

    /** 屏幕配置 **/
    val configuration get() = context.resources.configuration!!

    /** 屏幕方向 **/
    val orientation get() = configuration.orientation

    /** 屏幕像素密度 **/
    val screenDensity: Float get() = Resources.getSystem().displayMetrics.density

    /** 返回以每英寸点数表示的屏幕密度 **/
    val screenDensityDpi: Int get() = Resources.getSystem().displayMetrics.densityDpi

    /** 返回X维度中屏幕每英寸的精确物理像素 **/
    val screenXDpi: Float get() = Resources.getSystem().displayMetrics.xdpi

    /** 返回X维度中屏幕每英寸的精确物理像素 **/
    val screenYDpi: Float get() = Resources.getSystem().displayMetrics.ydpi

    /**
     * 返回屏幕上视图的X坐标
     */
    fun getViewX(view: View): Int = IntArray(2).also { view.getLocationOnScreen(it) }[0]

    /**
     * 返回屏幕上视图的Y坐标
     */
    fun getViewY(view: View): Int = IntArray(2).also { view.getLocationOnScreen(it) }[1]

    /**
     * 设置全屏
     * @param activity [Activity]
     */
    fun setFullScreen(activity: Activity?) = setFullScreen(activity?.window)

    /**
     * 设置全屏
     * @param window 窗口
     */
    @Suppress("Deprecation")
    fun setFullScreen(window: Window?) {
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            // API 30 以上写法
                it.insetsController?.hide(WindowInsets.Type.statusBars())
            else
            // API 30 以前写法
                it.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    /**
     * 取消全屏设置
     * @param activity [Activity]
     */
    fun setNonFullScreen(activity: Activity?) = setNonFullScreen(activity?.window)

    /**
     * 取消全屏设置
     * @param window 窗口
     */
    @Suppress("Deprecation")
    fun setNonFullScreen(window: Window?) {
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            // API 30 以上写法
                it.insetsController?.show(WindowInsets.Type.statusBars())
            else
            // API 30 以前写法
                it.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    /**
     * 切换全屏状态
     * @param activity [Activity]
     */
    fun toggleFullScreen(activity: Activity) {
        val isFullScreen = isFullScreen(activity)
        if (isFullScreen) setNonFullScreen(activity)
        else setFullScreen(activity)
    }

    /**
     * 是否是全屏显示
     * @param window 窗口
     */
    fun isFullScreen(window: Window?): Boolean {
        return window?.let {

            val controller = ViewCompat.getWindowInsetsController(window.decorView)
//            controller.show()
//            WindowInsets.Type.ime()
//            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                val softInputMode = it.attributes.softInputMode
//                !isVisibleInsetsType(WindowInsets.Type.statusBars(), softInputMode)
//            } else {
//                @Suppress("Deprecation")
//                val fullFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
//                it.attributes.flags and fullFlag == fullFlag
//            }
//            it.insetsController?.hide()


            @Suppress("Deprecation")
            val fullFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
            it.attributes.flags and fullFlag == fullFlag
        } ?: false
    }

    /**
     * 是否是全屏显示
     * @param activity [Activity]
     */
    fun isFullScreen(activity: Activity?) = isFullScreen(activity?.window)


//    /**
//     * Return whether screen is full.
//     *
//     * @param activity The activity.
//     * @return `true`: yes<br></br>`false`: no
//     */
//    fun isFullScreen(activity: Activity): Boolean {
//
//
//        val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
//        return activity.window.attributes.flags and fullScreenFlag == fullScreenFlag
//    }

    /**
     * Set the screen to landscape.
     *
     * @param activity The activity.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * Set the screen to portrait.
     *
     * @param activity The activity.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 屏幕是否为横屏
     */
    fun isLandscape(): Boolean = orientation == Configuration.ORIENTATION_LANDSCAPE

    /**
     * 屏幕是否为竖屏
     */
    fun isPortrait(): Boolean = orientation == Configuration.ORIENTATION_PORTRAIT

    /**
     * 获取屏幕的旋转角度
     * @param activity [Activity]
     */
    @Suppress("Deprecation")
    fun getScreenRotation(activity: Activity?): Int {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.display
        } else {
            activity?.windowManager?.defaultDisplay
        }
        return display?.let {
            when (it.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
        } ?: 0
    }

//    /**
//     * Return the bitmap of screen.
//     *
//     * @param activity The activity.
//     * @return the bitmap of screen
//     */
//    fun screenShot(activity: Activity): Bitmap? {
//        return screenShot(activity, false)
//    }
//
//    /**
//     * Return the bitmap of screen.
//     *
//     * @param activity          The activity.
//     * @param isDeleteStatusBar True to delete status bar, false otherwise.
//     * @return the bitmap of screen
//     */
//    fun screenShot(activity: Activity, isDeleteStatusBar: Boolean): Bitmap? {
//        val decorView = activity.window.decorView
//        val bmp = UtilsBridge.view2Bitmap(decorView)
//        val dm = DisplayMetrics()
//        activity.windowManager.defaultDisplay.getMetrics(dm)
//        return if (isDeleteStatusBar) {
//            val statusBarHeight = UtilsBridge.getStatusBarHeight()
//            Bitmap.createBitmap(
//                bmp,
//                0,
//                statusBarHeight,
//                dm.widthPixels,
//                dm.heightPixels - statusBarHeight
//            )
//        } else {
//            Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
//        }
//    }

    /**
     * Return whether screen is locked.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    /**
     * 屏幕是否锁定
     */
    fun isScreenLock(): Boolean {
        val km = Get.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return km.isKeyguardLocked
    }

    /**
     * Checks whether the specified type is considered to be part of visible insets.
     * @hide
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun isVisibleInsetsType(type: Int, softInputModeFlags: Int): Boolean {
        val softInputMode = softInputModeFlags and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST
        return (type and WindowInsets.Type.systemBars() != 0
                || softInputMode != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING && type and WindowInsets.Type.ime() != 0)
    }
}