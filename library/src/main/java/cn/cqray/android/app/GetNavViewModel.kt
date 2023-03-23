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
import cn.cqray.android.anim.AnimUtils

import cn.cqray.android.anim.GetFragmentAnimator
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.log.GetLog
import com.blankj.utilcode.util.ActivityUtils
import java.util.*

/**
 * [GetNavDelegate]逻辑实现，由[FragmentActivity]持有
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate", "Unused"
)
internal class GetNavViewModel(owner: LifecycleOwner) : GetViewModel(owner) {

    init {
        if (owner !is FragmentActivity || owner !is GetNavProvider) {
            throw IllegalArgumentException(
                String.format(
                    "%s can only get by FragmentActivity which implements %s.",
                    javaClass.simpleName,
                    GetNavProvider::class.java.simpleName
                )
            )
        }
    }

    /** 回退栈  */
    private val backStack = Stack<String>()

    /** 持有的Activity **/
    private val activity get() = lifecycleOwner as FragmentActivity

    /** Fragment管理器 **/
    private val fragmentManager get() = activity.supportFragmentManager

    /** 容器Id  */
    var containerId = View.NO_ID
        private set

    /** 栈顶的Fragment **/
    val topFragment
        get() = if (backStack.isEmpty()) null
        else fragmentManager.findFragmentByTag(backStack.peek())

    /** 获取堆栈的Fragment列表 **/
    val fragments
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
            if (!fragment.onBackPressedGet()) {
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
        val fragment = factory.instantiate(activity.classLoader, intent.toClass.name)
        // 设置参数
        fragment.arguments = intent.arguments.also { it.putString(FRAGMENT_ID_TAG, uuid) }
        // 返回Fragment
        return fragment
    }

    /**
     * 获取Fragment对应的标识，结构为[类名-UUID]
     * @param fragment fragment对象
     */
    fun getFragmentTag(fragment: Fragment): String {
        val className = fragment::class.java.name
        val arguments = fragment.arguments
        return "$className-" + arguments?.getString(FRAGMENT_ID_TAG)
    }

    /**
     * 获取Fragment对应的启动动画时长
     * @param fragment fragment对象
     */
    fun getFragmentEnterAnimDuration(fragment: Fragment): Int {
        val arguments = fragment.arguments
        return arguments?.getInt(FRAGMENT_ANIM_DURATION_Tag) ?: 0
    }

    /**
     * 是否是最先加载的Fragment
     * @param clazz fragment对应class
     */
    fun isRootFragment(clazz: Class<*>): Boolean {
        if (!GetUtils.isGetFragmentClass(clazz)) return false
        if (backStack.isEmpty()) return false
        return backStack.firstElement().split("-")[0] == clazz.name
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent) {
        this.containerId = containerId
        to(intent)
    }

    /**
     * 启动界面
     * @param intent [GetIntent]
     */
    fun to(intent: GetIntent) {
        if (intent.isSingleTop && topFragment?.javaClass == intent.toClass) {
            // Fragment满足SingleTop的拦截条件
            // 则不进行后续操作
            return
        }
//        // 检查Fragment是否满足SingleTop的拦截条件
//        if (checkFragmentSingleTop(intent)) return
//        // 检查Fragment是否满足SingleTask的拦截条件
//        if (checkFragmentSingleTask(intent)) return
        // 检查容器ID
        checkContainerID()
        // 处理回退
        backTo(intent.backToClass, intent.toClass, intent.isBackInclusive)
        // 跳转Activity
        if (GetUtils.isActivityClass(intent.toClass)) {
            val actIntent = Intent(activity, intent.toClass)
            actIntent.putExtras(intent.arguments)
            if (intent.isSingleTop) actIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            Get.toActivity(actIntent)
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
                // 计算并设置Fragment动画时长
                val duration = AnimUtils.getAnimDurationFromResource(enter)
                fragment.arguments?.putInt(FRAGMENT_ANIM_DURATION_Tag, duration)
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
        runCatching {
            // 提交事务
            ft.setReorderingAllowed(true)
            ft.commit()
            // 事务提交成功，则加入回退栈
            backStack.add(fTag)
        }.onFailure {
            // 打印错误日志
            logE("to", "to start [${intent.toClass.simpleName}] failed.", it)
        }
    }

    /** 回退 **/
    fun back() = if (backStack.size <= 1) activity.finish() else popBackStack(backStack.peek())

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面，默认true
     */
    fun backTo(back: Class<*>, inclusive: Boolean) = backTo(back, null, inclusive)

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
        val isBackToActivity = GetUtils.isActivityClass(back)
        // 目标界面是否是Fragment
        val isToFragment = GetUtils.isGetFragmentClass(to)
        // 回退到指定的Activity
        if (isBackToActivity) {
            ActivityUtils.finishToActivity(back as Class<out Activity>, inclusive)
            return
        }
        // 回退至根Fragment（包含），不启动新的Fragment，则销毁Activity
        if (isRootFragment(back) && inclusive && !isToFragment) {
            activity.finish()
            return
        }
        // 需要回到的Fragment标志位
        var backIndex = -1
        // 查找对应的Fragment
        for (i in backStack.indices) {
            // 匹配到对应的Fragment
            if (backStack[i].split("-")[0] == back.name) {
                backIndex = i + if (inclusive) 0 else 1
                break
            }
        }
        // 回退出栈
        popBackStack(backStack.getOrNull(backIndex))
    }

    /**
     * 回退到指定标签（包含自身）
     */
    private fun popBackStack(name: String?) {
        name?.let {
            if (fragmentManager.isStateSaved) return
            runCatching {
                // FragmentManager回退到指定标签（包含自身）
                fragmentManager.popBackStackImmediate(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                // FragmentManager回退成功，则清除回退栈指定数据
                for (i in backStack.indices.reversed()) {
                    // 移除缓存一直到指定的名称位置
                    if (name == backStack.removeAt(i)) break
                }
            }.onFailure {
                // 打印错误日志
                logE("back", "pop back stack error.", it)
            }
        }
    }

    /**
     * 检查[Fragment]是否满足[GetIntent.SINGLE_TOP]的拦截条件
     */
    private fun checkFragmentSingleTop(intent: GetIntent): Boolean {
        if (intent.isSingleTop) {
            return topFragment?.javaClass == intent.toClass
        }
        return false
    }
    /**
     * 检查容器ID是否设置
     */
    private fun checkContainerID() {
        if (containerId == View.NO_ID) {
            // 未调用loadRootFragment，抛出异常
            throw IllegalStateException("Did you forget call loadRootFragment?")
        }
    }

    /**
     * 获取Fragment动画
     * @param fragment 动画作用的Fragment
     * @param intent 启动传参
     */
    private fun getFragmentAnimator(fragment: Fragment, intent: GetIntent): GetFragmentAnimator {
        // 从下至上，一级级获取优先级高的动画设置
        return intent.fragmentAnimator
        // 意图中无动画，则使用当前Fragment的动画
            ?: (fragment as? GetNavProvider)?.onCreateFragmentAnimator()
            // Fragment无动画，则使用Activity的动画
            ?: (lifecycleOwner as? GetNavProvider)?.onCreateFragmentAnimator()
            // Activity无动画，则使用全局动画
            ?: Get.init.fragmentAnimator
    }

    /**
     * 打印日志
     * @param method 所在方法
     * @param text 日志内容
     */
    private fun logE(method: String, text: String, th: Throwable? = null) {
        val tag = GetNavDelegate::class.java.simpleName
        // 日志内容
        val message = " \n" +
                "       Owner Class: ${lifecycleOwner::class.java.name}\n" +
                "       Used Method: $method\n" + "" +
                "       Content: $text"
        // 打印日志
        if (th == null) GetLog.eTag(tag, message)
        else GetLog.eTag(tag, message, th)
    }

    private companion object {

        /** Fragment ID 关键字 **/
        const val FRAGMENT_ID_TAG = "Get:Fragment_id"

        /** Fragment 动画时长关键字 **/
        const val FRAGMENT_ANIM_DURATION_Tag = "Get:Fragment_anim_duration"
    }
}