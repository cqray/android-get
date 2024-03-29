package cn.cqray.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import cn.cqray.android.app.GetInit
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.GetNavProvider
import cn.cqray.android.tip.GetTip
import cn.cqray.android.tip.GetTipInit
import cn.cqray.android.util.Contexts
import com.blankj.utilcode.util.ActivityUtils

/**
 * [Get]框架
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
object Get {

    /** [GetInit]配置 **/
    @JvmStatic
    val init = GetInit()

    /** 获取顶部[Activity] **/
    @JvmStatic
    val topActivity: Activity? get() = ActivityUtils.getTopActivity()

    /** 获取顶部实现了[GetNavProvider]的[Activity] **/
    val topGetActivity: GetNavProvider?
        get() {
            val activities = ActivityUtils.getActivityList()
            for (act in activities.reversed()) {
                if (act is GetNavProvider) return act
            }
            return null
        }

    /** 获取[Application]实例 **/
    @JvmStatic
    val application get() = _Get.application

    @JvmStatic
    val context: Context get() = topActivity ?: application.applicationContext

    /**
     * 在UI线程上延时执行程序
     * @param runnable  需要执行的内容
     * @param delayed   延时时长(ms)
     */
    @JvmStatic
    @JvmOverloads
    fun runOnUiThreadDelayed(runnable: Runnable, delayed: Int? = null) = _Get.runOnUiThreadDelayed(runnable, delayed)

    /**
     * 跳转界面
     * @param target 目标界面
     * @param callback 回调
     */
    @JvmStatic
    @JvmOverloads
    fun push(target: Class<*>, callback: Function1<Bundle, Unit>? = null): Unit =
        run { topGetActivity?.push(GetIntent(target), callback) }

    /**
     * 跳转界面
     * @param intent 意图
     * @param callback 回调
     */
    @JvmStatic
    @JvmOverloads
    fun push(intent: GetIntent, callback: Function1<Bundle, Unit>? = null): Unit =
        run { topGetActivity?.push(intent, callback) }

    /**
     * 回退
     */
    @JvmStatic
    fun pop(): Unit = run { topGetActivity?.pop() }

    /**
     * 回退到指定的界面
     * @param target 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    @JvmStatic
    @JvmOverloads
    fun popTo(target: Class<*>, inclusive: Boolean = true) = topGetActivity?.popTo(target, inclusive)

    /**
     * 显示提示
     * @param text 文本
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    @JvmStatic
    @JvmOverloads
    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) = GetTip.show(text, null, hideCallback, showCallback)

    /**
     * 显示提示
     * @param text 文本
     * @param init 配置
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    @JvmStatic
    @JvmOverloads
    fun showTip(
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) = GetTip.show(text, init, hideCallback, showCallback)

    /**
     * 显示提示
     * @param id 文本资源ID
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    @JvmStatic
    @JvmOverloads
    fun showTip(
        @StringRes id: Int,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) = GetTip.show(Contexts.getString(id), null, hideCallback, showCallback)

    /**
     * 显示提示
     * @param id 文本资源ID
     * @param init 配置
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    @JvmStatic
    @JvmOverloads
    fun showTip(
        @StringRes id: Int,
        init: GetTipInit?,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) = GetTip.show(Contexts.getString(id), init, hideCallback, showCallback)
}
