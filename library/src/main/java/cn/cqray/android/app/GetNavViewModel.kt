package cn.cqray.android.app

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import cn.cqray.android.Get
import cn.cqray.android.anim.AnimUtils
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.log.GetLog
import com.blankj.utilcode.util.ActivityUtils
import java.util.*

/**
 * [GetNavDelegate]逻辑实现，由[FragmentActivity]持有
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "StaticFieldLeak",
    "Unchecked_cast",
    "Unused"
)
internal class GetNavViewModel : ViewModel() {

    /** 绑定的Activity **/
    var activity: FragmentActivity? = null
        set(value) {
            // 因为涉及到内存泄漏的问题，所以需要自定销毁资源
            value?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    activity = null
                }
            })
            field = value
        }

    /** 容器Id  */
    private var containerId = View.NO_ID

    /** [Fragment]记录 **/
    private val backStack = Stack<Fragment>()

    /** Fragment管理器 **/
    val fragmentManager get() = activity!!.supportFragmentManager

    /** 栈顶的Fragment **/
    val topFragment get() = backStack.lastOrNull()

    /** 返回Fragment列表 **/
    val fragments get() = mutableListOf<Fragment>().also { it.addAll(backStack) }

    /** 回退实现 **/
    fun onBackPressed() {
        val fragment = topFragment
        if (fragment is GetNavProvider) {
            if (!fragment.onBackPress()) {
                // 有多个Fragment，则直接回退
                if (backStack.size > 1) pop()
                // 只有0-1个Fragment，则检查Activity的拦截
                else if (!(activity as GetNavProvider).onBackPress()) pop()
            }
        } else if (!(activity as GetNavProvider).onBackPress()) pop()
    }

    /**
     * 获取Fragment对应的启动动画时长
     * @param fragment fragment对象
     */
    fun getFragmentEnterAnimDuration(fragment: Fragment): Int {
        val arguments = fragment.arguments
        return arguments?.getInt(FRAGMENT_ANIM_DURATION_TAG) ?: 0
    }

    /**
     * 是否是最先加载的Fragment
     * @param clazz fragment对应class
     */
    fun isRootFragment(clazz: Class<*>) = backStack.firstOrNull()?.javaClass == clazz

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent) {
        this.containerId = containerId
        start(intent)
    }

    /**
     * 启动界面
     * @param intent [GetIntent]
     */
    fun start(intent: GetIntent) {
        // 检查是否是启动Activity
        if (checkActivity(intent)) return
        // 检查是否满足Fragment拦截条件
        if (checkFragment(intent)) return
        // 检查容器ID
        if (containerId == View.NO_ID) {
            // 未调用loadRootFragment，抛出异常
            throw IllegalStateException("Did you forget call loadRootFragment?")
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
                fragment.arguments?.putInt(FRAGMENT_ANIM_DURATION_TAG, duration)
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
            // 事务提交成功，则Fragment加入回退栈
            backStack.add(fragment)
        }.onFailure {
            // 打印错误日志
            logE("to", "start [${intent.toClass.simpleName}] failed.", it)
        }
    }

    /** 回退 **/
    fun pop() {
        // 只剩根Fragment，则直接结束Activity
        if (backStack.size <= 1) activity?.finish()
        // 否则回退当前Fragment
        else popBackStack(backStack.lastOrNull())
    }

    /**
     * 回退到指定的界面
     * @param target 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    fun popTo(target: Class<*>, inclusive: Boolean) {
        // 回退到指定的Activity
        if (GetUtils.isActivityClass(target)) {
            ActivityUtils.finishToActivity(target as Class<out Activity>, inclusive)
            return
        }
        // 回退至根Fragment（包含），不启动新的Fragment，则销毁Activity
        if (isRootFragment(target) && inclusive) {
            activity?.finish()
            return
        }
        // 查找回退Class在的Fragment在记录中的对应的位置（最后一个匹配的位置）
        val index = backStack.indexOfLast { it.javaClass == target }.let {
            // 未找到
            if (it == -1) -1
            // 不包含自身则索引+1
            else it + (if (inclusive) 0 else 1)
        }
        // 回退到指定的Fragment
        popBackStack(backStack.getOrNull(index))
    }

    /**
     * 回退到指定[Fragment]（包含自身）
     * @param fragment [Fragment]实例
     */
    private fun popBackStack(fragment: Fragment?) {
        fragment?.let {
            // FragmentManager处理无效状态
            if (fragmentManager.isStateSaved) return
            runCatching {
                // 处理回退
                fragmentManager.popBackStackImmediate(
                    getFragmentTag(it),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                // 从回退栈中手动移除
                for (i in backStack.indices.reversed()) {
                    // 倒序移除，直到指定的Fragment
                    if (backStack.removeAt(i) == it) break
                }
            }.onFailure {
                // 打印错误日志
                logE("back", "pop back stack error.", it)
            }
        }
    }

    /**
     * 检查是否是启动[Activity]，是则拦截
     * @param intent 意图
     */
    private fun checkActivity(intent: GetIntent): Boolean {
        if (GetUtils.isActivityClass(intent.toClass)) {
            val actIntent = Intent(activity, intent.toClass)
            actIntent.putExtras(intent.arguments)
            actIntent.addFlags(intent.launchMode)
            if (intent.launchMode != 0) {
                // 重复加载，传入新的参数
                val activity = ActivityUtils.getActivityList()?.findLast { it.javaClass == intent.toClass }
                (activity as? GetNavProvider)?.let {
                    activity.intent.putExtras(intent.arguments)
                    it.onNewArguments(intent.arguments)
                }
            }
            ActivityUtils.startActivity(actIntent)
            return true
        }
        return false
    }

    /**
     * 检查是否满足[Fragment]拦截条件
     * 条件1：[GetIntent.SINGLE_TOP]并匹配
     * 条件2：[GetIntent.SINGLE_TASK]并匹配
     * @param intent 意图
     */
    private fun checkFragment(intent: GetIntent): Boolean {
        when (intent.launchMode) {
            GetIntent.SINGLE_TOP -> {
                // 匹配到相同的Fragment
                if (topFragment?.javaClass == intent.toClass) {
                    // 重复加载，传入新的参数
                    topFragment!!.requireArguments().putAll(intent.arguments)
                    (topFragment as GetNavProvider).onNewArguments(intent.arguments)
                    return true
                }
            }
            GetIntent.SINGLE_TASK -> {
                // 倒序遍历
                for (i in backStack.indices.reversed()) {
                    val fragment = backStack[i]
                    // 匹配到相同的Fragment
                    if (fragment.javaClass == intent.toClass) {
                        popBackStack(backStack.getOrNull(i + 1))
                        // 重复加载，传入新的参数
                        fragment.requireArguments().putAll(intent.arguments)
                        (fragment as GetNavProvider).onNewArguments(intent.arguments)
                        return true
                    }
                }
            }
            else -> return false
        }
        return false
    }

    /**
     * 获取Fragment动画
     * @param fragment 动画作用的Fragment
     * @param intent 启动传参
     */
    private fun getFragmentAnimator(fragment: Fragment, intent: GetIntent): FragmentAnimator {
        // 从下至上，一级级获取优先级高的动画设置
        return intent.fragmentAnimator
        // 意图中无动画，则使用当前Fragment的动画
            ?: (fragment as? GetNavProvider)?.onCreateFragmentAnimator()
            // Fragment无动画，则使用Activity的动画
            ?: (activity as? GetNavProvider)?.onCreateFragmentAnimator()
            // Activity无动画，则使用全局动画
            ?: Get.init.fragmentAnimator
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param intent intent对象
     */
    private fun generateFragment(intent: GetIntent): Fragment {
        // 生成UUID
        val uuid = UUID.randomUUID().toString().replace("-", "")
        // Fragment工厂
        val factory = fragmentManager.fragmentFactory
        // 创建Fragment
        val fragment = factory.instantiate(activity!!.classLoader, intent.toClass.name)
        // 销毁时移除Fragment
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                backStack.remove(owner as Fragment)
            }
        })
        // 设置参数
        fragment.arguments = intent.arguments.also { it.putString(FRAGMENT_ID_TAG, uuid) }
        // 返回Fragment
        return fragment
    }

    /**
     * 获取Fragment对应的标识，结构为[类名-UUID]
     * @param fragment fragment对象
     */
    private fun getFragmentTag(fragment: Fragment): String {
        val className = fragment::class.java.name
        val arguments = fragment.arguments
        return "$className-" + arguments?.getString(FRAGMENT_ID_TAG)
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
                "       Owner Class: ${activity?.javaClass?.name}\n" +
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
        const val FRAGMENT_ANIM_DURATION_TAG = "Get:Fragment_anim_duration"
    }
}