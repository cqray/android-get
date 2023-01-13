package cn.cqray.android.app

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get

import cn.cqray.android.anim.DefaultVerticalAnimator
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.exc.ExceptionDispatcher
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.log.GetLog

import java.util.*
import kotlin.Exception

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GetNavViewModel(owner: LifecycleOwner) : GetViewModel(owner) {

    /** Fragment ID 关键字 **/
    private val fragmentIdKey = "Get:fragmentId"

    /** 容器Id  */
    private var containerId = View.NO_ID

    /** 进入动画时长  */
    private var openEnterAnimDuration = 0

    /** 回退栈  */
    private val backStack = Stack<String>()

    private val getChecker = GetChecker()

    init {
        if (owner !is FragmentActivity || owner !is GetNavProvider) {
            val exc = String.format(
                "%s can only get by FragmentActivity which implements %s.",
                GetNavViewModel::class.java.name,
                GetNavProvider::class.java.name
            )
            throw RuntimeException(exc)
        }
    }

    /** 栈顶的Fragment **/
    val topFragment: Fragment?
        get() = if (backStack.isEmpty()) null
        else fragmentManager.findFragmentByTag(backStack.peek())

    /** 动画时长 **/
    val enterAnimDuration: Int get() = openEnterAnimDuration

    /** 容器View **/
    val containerView: View get() = activity.findViewById(containerId)

    /** 持有的Activity **/
    val activity: FragmentActivity get() = lifecycleOwner as FragmentActivity

    /** Fragment管理器 **/
    val fragmentManager: FragmentManager get() = activity.supportFragmentManager

    /** 获取堆栈的Fragment列表 **/
    val fragments: MutableList<Fragment>
        get() = MutableList(backStack.size) {
            fragmentManager.findFragmentByTag(backStack[it])!!
        }

    /**
     * 资源回收
     */
    override fun onCleared() {
        super.onCleared()
        backStack.clear()
    }

    /** 回退实现 **/
    fun onBackPressed() {
        val fragment = topFragment
        if (fragment is GetNavProvider) {
            if (fragment.onBackPressedGet()) {
                // 有多个Fragment，则直接回退
                if (backStack.size > 1) back()
                // 只有0-1个Fragment，则检查Activity的拦截
                else if (!(activity as GetNavProvider).onBackPressedGet()) back()
            }
        } else if (!(activity as GetNavProvider).onBackPressedGet()) back()
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param intent intent对象
     */
    fun generateFragment(intent: GetIntent): Fragment {
        // 生成UUID
        val uuid = UUID.randomUUID().toString().replace("-", "")
        // Fragment工厂
        val factory = fragmentManager.fragmentFactory
        // 创建Fragment
        val fragment = factory.instantiate(activity.classLoader, intent.toClass!!.name)
        // 获取参数
        val arguments = intent.arguments
        // 设置ID
        arguments.putString(fragmentIdKey, uuid)
        // 设置参数
        fragment.arguments = arguments
        // 返回Fragment
        return fragment
    }

    /**
     * 获取Fragment对应的标识
     * @param fragment fragment对象
     */
    fun getFragmentTag(fragment: Fragment): String {
        val className = fragment::class.java.name
        val arguments = fragment.arguments
        return "$className-" + arguments?.getString(fragmentIdKey)
    }

    /**
     * 是否是最先加载的Fragment
     * @param cls fragment对应class
     */
    fun isRootFragment(cls: Class<*>): Boolean {
        if (backStack.isEmpty()) return false
        return backStack.firstElement().split("-").toTypedArray()[0] == cls.name
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int?, intent: GetIntent) {
        this.containerId = containerId ?: View.NO_ID
        to(intent)
    }

    /**
     * 启动界面
     * @param intent [GetIntent]
     */
    fun to(intent: GetIntent) {
        // 拦截快速点击
        if (getChecker.isFastClick()) return
        // 检查容器就绪
        if (!checkContainerReady()) return
        // 检查To Class 就绪
        if (!checkToClassReady(intent)) return
        // 处理回退
        backTo(intent.backToClass, intent.toClass, intent.isPopToInclusive)
        // 跳转Activity
        if (Activity::class.java.isAssignableFrom(intent.toClass!!)) {
            val actIntent = Intent(activity, intent.toClass)
            actIntent.putExtras(intent.arguments)
            GetManager.toActivity(actIntent)
            return
        }
        // 跳转Fragment
        // 获取Fragment事务
        val ft = fragmentManager.beginTransaction()
        // 生成Fragment
        val fragment = generateFragment(intent)
        // 如果回退栈不为空，则需要显示动画
        if (backStack.isNotEmpty()) {
            // 设置Fragment动画
            val animator = getFragmentAnimator(fragment, intent)
            with(animator) {
                // 设置自定义动画
                ft.setCustomAnimations(enter, exit, popEnter, popExit)
                // 计算进入动画时长
                openEnterAnimDuration = GetUtils.getAnimDurationFromResource(enter)
            }
        }
        // 隐藏当前正在显示的Fragment
        fragmentManager.primaryNavigationFragment?.let {
            // 改变生命周期状态
            ft.setMaxLifecycle(it, Lifecycle.State.STARTED)
        }
        // 获取FragmentTag
        val fTag = getFragmentTag(fragment)
        // 添加Fragment
        ft.add(containerId, fragment, fTag)
        // 设置初始化生命周期
        ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        // 设置当前Fragment
        ft.setPrimaryNavigationFragment(fragment)
        // 加入BackStack
        ft.addToBackStack(fTag)
        try {
            // 提交事务
            ft.setReorderingAllowed(true)
            ft.commit()
            // 事务提交成功，则加入回退栈
            backStack.add(fTag)
        } catch (exc: Exception) {
            // 打印错误日志
            printLog("to", "to start [${intent.toClass!!.simpleName}] failed.", exc)
        }
    }

    /** 回退 **/
    fun back() = if (backStack.size <= 1) activity.finish() else popBackStack(backStack.peek())

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面，默认true
     */
    fun backTo(back: Class<*>?, inclusive: Boolean?) = backTo(back, null, inclusive ?: true)

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param to 需要启动的界面[Class]
     * @param inclusive 是否包含指定回退的界面
     */
    @Suppress("unchecked_cast")
    private fun backTo(back: Class<*>?, to: Class<*>?, inclusive: Boolean) {
        // 回退目标为NULL，不处理
        if (back == null) return
        // 是否是回退到Activity
        val isBackToActivity = Activity::class.java.isAssignableFrom(back)
        // 目标界面是否是Fragment
        val isToFragment = GetUtils.isGetFragmentClass(to)
        // 回退到指定的Activity
        if (isBackToActivity) {
            GetManager.backToActivity(back as Class<out Activity>, inclusive)
            return
        }
        // 回退至根Fragment（包含），不启动新的Fragment，则销毁Activity
        if (isRootFragment(back) && inclusive && !isToFragment) {
            activity.finish()
            return
        }
        // 需要回到的Fragment标识
        var backTag: String? = null
        // 查找对应的Fragment
        for (i in backStack.indices) {
            val fTag = backStack[i]
            // 匹配到对应的Fragment
            if (fTag.split("-").toTypedArray()[0] == back.name) {
                backTag =
                    if (!inclusive)
                        if (i == backStack.size - 1) null
                        else backStack[i + 1]
                    else fTag
                break
            }
        }
        // 回退出栈
        popBackStack(backTag)
    }

    /**
     * 回退到指定标签（包含自身）
     */
    private fun popBackStack(name: String?) {
        name?.let {
            if (fragmentManager.isStateSaved) return
            try {
                // FragmentManager回退到指定标签（包含自身）
                fragmentManager.popBackStackImmediate(
                    name, FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                // FragmentManager回退成功，则清除回退栈指定数据
                for (i in backStack.indices.reversed()) {
                    if (name == backStack.removeAt(i)) {
                        break
                    }
                }
            } catch (exc: Exception) {
                // 打印错误日志
                printLog("back", "pop back stack error.", exc)
            }
        }
    }

    /**
     * 检查容器是否准备就绪
     */
    private fun checkContainerReady() = (containerId != View.NO_ID).also {
        if (it) return true
        ExceptionDispatcher.dispatchStarterThrowable(
            null, "请先调用loadRootFragment()。", "未设置containerId，便开始调用to()方法。"
        )
    }

    /**
     * 检查目标界面是否准备就绪
     * @param intent 意图
     */
    private fun checkToClassReady(intent: GetIntent): Boolean {
        val to = intent.toClass
        val valid = GetUtils.isGetActivityClass(to) || GetUtils.isGetFragmentClass(to)
        return valid.also {
            if (it) return true
            ExceptionDispatcher.dispatchStarterThrowable(
                null, "未设置目标Class。", "to()跳转界面时没设置目标Class。"
            )
        }
    }

    /**
     * 获取Fragment动画
     * @param fragment 动画作用的Fragment
     * @param intent 启动传参
     */
    private fun getFragmentAnimator(fragment: Fragment, intent: GetIntent): FragmentAnimator {
        // 从下至上，一级级获取优先级高的动画设置
        return intent.fragmentAnimator ?: (fragment as? GetNavProvider)?.onCreateFragmentAnimator()
        ?: (lifecycleOwner as? GetNavProvider)?.onCreateFragmentAnimator() ?: Get.init.fragmentAnimator
        ?: DefaultVerticalAnimator()
    }

    /**
     * 打印日志
     * @param method 所在方法
     * @param text 日志内容
     * @param th 异常信息
     */
    private fun printLog(method: String, text: String, th: Throwable? = null) {
        // 日志内容
        val message = " \n" +
                "       Owner Class: ${lifecycleOwner::class.java.name}\n" +
                "       Used Method: $method\n" + "" +
                "       Content: $text"
        // 打印日志
        GetLog.e(GetNavDelegate::class.java, message, th)
    }
}