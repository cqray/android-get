package cn.cqray.android.app2

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.Get

/**
 * [Get]导航功能提供器
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetNavProvider : GetProvider {

    /**
     * 获取[GetNavDelegate]
     */
    val navDelegate: GetNavDelegate get() = GetDelegate.get(this, GetNavProvider::class.java)

    /**
     * 创建Fragment动画
     * @return FragmentAnimator实例
     */
    fun onCreateFragmentAnimator(): FragmentAnimator? = null

    /**
     * 回退事件拦截，类似[Activity.onBackPressed]
     * @return true 拦截 false 不拦截
     */
    fun onBackPress(): Boolean = false

    /**
     * 进入动画结束
     */
    fun onEnterAnimEnd() {}

    /**
     * 懒加载
     */
    fun onLazyLoad() {}

    /**
     * [Fragment]实现类似[Activity.onNewIntent]功能
     * @param arguments 重新传入参数
     */
    fun onNewArguments(arguments: Bundle?) {}

    /**
     * 设置返回空白数据，类似[Activity.setResult]
     */
    fun setResult() = navDelegate.setResult(Bundle())

    /**
     * 设置返回数据，类似[Activity.setResult]
     * @param data [Bundle]返回数据
     */
    fun setResult(data: Bundle) = navDelegate.setResult(data)

    /**
     * 启动界面
     * @param to 目标界面[Class]
     */
    fun to(to: Class<*>) = navDelegate.to(GetIntent(to))

    /**
     * 启动界面
     * @param intent [GetIntent]
     */
    fun to(intent: GetIntent) = navDelegate.to(intent)

    /**
     * 启动界面
     * @param to 目标界面
     * @param callback 回调
     */
    fun to(to: Class<*>, callback: Function1<Bundle, Unit>?) = navDelegate.to(GetIntent(to), callback)

    /**
     * 启动界面
     * @param intent 意图
     * @param callback 回调
     */
    fun to(intent: GetIntent, callback: Function1<Bundle, Unit>?) = navDelegate.to(intent, callback)

    /**
     * 回退
     */
    fun back() = navDelegate.back()

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     */
    fun backTo(back: Class<*>) = navDelegate.backTo(back, true)

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    fun backTo(back: Class<*>, inclusive: Boolean) = navDelegate.backTo(back, inclusive)
}