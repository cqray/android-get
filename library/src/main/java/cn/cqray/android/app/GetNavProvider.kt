package cn.cqray.android.app

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
     * 跳转界面
     * @param target 目标界面[Class]
     */
    fun goto(target: Class<*>) = navDelegate.goto(GetIntent(target))

    /**
     * 跳转界面
     * @param intent [GetIntent]
     */
    fun goto(intent: GetIntent) = navDelegate.goto(intent)

    /**
     * 跳转界面
     * @param target 目标界面
     * @param callback 回调
     */
    fun goto(target: Class<*>, callback: Function1<Bundle, Unit>?) = navDelegate.goto(GetIntent(target), callback)

    /**
     * 跳转界面
     * @param intent 意图
     * @param callback 回调
     */
    fun goto(intent: GetIntent, callback: Function1<Bundle, Unit>?) = navDelegate.goto(intent, callback)

    /**
     * 回退
     */
    fun pop() = navDelegate.pop()

    /**
     * 回退到指定的界面
     * @param target 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     */
    fun popTo(target: Class<*>) = navDelegate.popTo(target, true)

    /**
     * 回退到指定的界面
     * @param target 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    fun popTo(target: Class<*>, inclusive: Boolean) = navDelegate.popTo(target, inclusive)
}