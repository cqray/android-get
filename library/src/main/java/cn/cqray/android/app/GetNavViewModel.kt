package cn.cqray.android.app

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import cn.cqray.android.anim.DefaultVerticalAnimator
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.exc.ExceptionDispatcher
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.util.ActivityUtils

import java.lang.Exception
import java.util.*

@Suppress("UNCHECKED_CAST")
class GetNavViewModel(owner: LifecycleOwner) : GetViewModel(owner) {

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

    /** 容器Id  */
    private var mContainerId = 0

    /** 进入动画时长  */
    private var mOpenEnterAnimDuration = 0

    /** 回退栈  */
    private val mBackStack = Stack<String>()

    private val getChecker = GetChecker()

    override fun onCleared() {
        super.onCleared()
        mBackStack.clear()
    }

    /** 栈顶的Fragment **/
    val topFragment: Fragment?
        get() = if (mBackStack.isEmpty()) {
            null
        } else fragmentManager.findFragmentByTag(mBackStack.peek())

    /** 动画时长 **/
    val enterAnimDuration: Int get() = mOpenEnterAnimDuration

    /** 容器View **/
    val containerView: View get() = activity.findViewById(mContainerId)

    /** 持有的Activity **/
    val activity: FragmentActivity get() = lifecycleOwner as FragmentActivity

    /** Fragment管理器 **/
    private val fragmentManager: FragmentManager get() = activity.supportFragmentManager

    /** 回退实现 **/
    fun onBackPressed() {
        val fragment = topFragment
        // 栈顶元素为空，说明没有调用LoadRootFragment。
        if (fragment == null) {
            // 处理onBackPressedGet拦截事件
            if (!(activity as GetNavProvider).onBackPressedGet()) {
                back()
            }
            return
        }
        // 处理onBackPressedGet()事件
        if (mBackStack.size <= 1) {
            // 检查Fragment是否拦截
            if (!(fragment as GetNavProvider).onBackPressedGet()) {
                // 检查Activity是否拦截
                if (!(activity as GetNavProvider).onBackPressedGet()) {
                    // 均未拦截则回退
                    back()
                }
            }
        } else {
            // 检查Fragment是否拦截
            if (!(fragment as GetNavProvider).onBackPressedGet()) {
                // 未拦截则回退
                back()
            }
        }
    }

    /**
     * 根据Intent生成相应的Fragment
     * @param intent intent对象
     */
    @Suppress("unused")
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
        arguments.putString(FRAGMENT_ID_KEY, uuid)
        // 设置参数
        fragment.arguments = arguments
        // 返回Fragment
        return fragment
    }

    /**
     * 获取Fragment对应的标识
     * @param fragment fragment对象
     */
    @Suppress("unused")
    fun getFragmentTag(fragment: Fragment): String {
        val arguments = fragment.arguments
        val id = if (arguments == null) "" else arguments.getString(FRAGMENT_ID_KEY)!!
        return fragment.javaClass.name + "-" + id
    }

    /**
     * 是否是最先加载的Fragment
     * @param cls fragment对应class
     */
    @Suppress("unused")
    fun isRootFragment(cls: Class<*>): Boolean {
        if (mBackStack.isEmpty()) {
            return false
        }
        val fTag = mBackStack.firstElement()
        return fTag.split("-").toTypedArray()[0] == cls.name
    }

    /**
     * 设置根Fragment
     * @param containerId 容器ID
     * @param intent [GetIntent]
     */
    fun loadRootFragment(@IdRes containerId: Int, intent: GetIntent) {
        mContainerId = containerId
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
            ActivityUtils.startActivity(actIntent)
            return
        }
        // 跳转Fragment
        // 获取Fragment事务
        val ft = fragmentManager.beginTransaction()
        // 生成Fragment
        val newFragment = generateFragment(intent)
        // 如果回退栈不为空，则需要显示动画
        if (mBackStack.isNotEmpty()) {
            // 设置Fragment动画
            val animator = getFragmentAnimator(newFragment, intent)
            ft.setCustomAnimations(
                animator.enter,
                animator.exit,
                animator.popEnter,
                animator.popExit
            )
            mOpenEnterAnimDuration = GetUtils.getAnimDurationFromResource(animator.enter)
        }
        // 隐藏当前正在显示的Fragment
        val current = fragmentManager.primaryNavigationFragment
        if (current != null) {
            ft.setMaxLifecycle(current, Lifecycle.State.STARTED)
            if (intent.backToClass != null) {
                ft.hide(current)
            }
        }
        // 获取FragmentTag
        val fTag = getFragmentTag(newFragment)
        // 添加Fragment
        ft.add(mContainerId, newFragment, fTag)
        // 设置初始化生命周期
        ft.setMaxLifecycle(newFragment, Lifecycle.State.RESUMED)
        // 设置当前Fragment
        ft.setPrimaryNavigationFragment(newFragment)
        // 加入BackStack
        ft.addToBackStack(fTag)
        try {
            // 提交事务
            ft.setReorderingAllowed(true)
            ft.commit()
            // 事务提交成功，则加入回退栈
            mBackStack.add(fTag)
        } catch (exc: Exception) { }
    }

    /** 回退 **/
    fun back() = if (mBackStack.size <= 1) activity.finish() else popBackStack(mBackStack.peek())

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含指定回退的界面
     */
    fun backTo(back: Class<*>?, inclusive: Boolean) = backTo(back, null, inclusive)

    /**
     * 回退到指定的界面
     * @param back 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param to 需要启动的界面[Class]
     * @param inclusive 是否包含指定回退的界面
     */
    private fun backTo(back: Class<*>?, to: Class<*>?, inclusive: Boolean) {
        // 回退目标为NULL，不处理
        if (back == null) return
        // 是否是回退到Activity
        val isBackToActivity = Activity::class.java.isAssignableFrom(back)
        // 目标界面是否是Fragment
        val isToFragment = GetUtils.isGetFragmentClass(to)
        // 回退到指定的Activity
        if (isBackToActivity) {
            //ActivityUtils.finishToActivity((back as Class<out Activity?>), inclusive)
            return
        }
        // 回退至根Fragment（包含），不启动新的Fragment，则销毁Activity
        if (isRootFragment(back) && inclusive && !isToFragment) {
            activity.finish()
            return
        }
        // 目标Fragment
        var toFragment: String? = null
        // 查找对应的Fragment
        for (i in mBackStack.indices) {
            val fTag = mBackStack[i]
            if (fTag.split("-").toTypedArray()[0] == back.name) {
                // 匹配到对应的Fragment
                toFragment =
                        // 不包含
                    if (!inclusive)
                        if (i == mBackStack.size - 1) null
                        else mBackStack[i + 1]
                    // 包含
                    else fTag
                break
            }
        }
        // 回退出栈
        popBackStack(toFragment)
    }

    /**
     * 回退到指定标签（包含自身）
     */
    private fun popBackStack(name: String?) {
        if (TextUtils.isEmpty(name)) return
        if (!fragmentManager.isStateSaved) {
            try {
                // FragmentManager回退到指定标签（包含自身）
                fragmentManager.popBackStackImmediate(
                    name,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                // FragmentManager回退成功，则清除回退栈指定数据
                for (i in mBackStack.indices.reversed()) {
                    if (name == mBackStack.removeAt(i)) {
                        break
                    }
                }
            } catch (exc: Exception) {
            }
        }
    }

    /**
     * 检查容器是否准备就绪
     */
    private fun checkContainerReady(): Boolean {
        if (mContainerId == 0) {
            ExceptionDispatcher.dispatchStarterThrowable(
                null,
                "请先调用loadRootFragment()。",
                "未设置ContainerId，便开始调用start()方法。"
            )
            return false
        }
        return true
    }

    /**
     * 检查目标界面是否准备就绪
     * @param intent 意图
     */
    private fun checkToClassReady(intent: GetIntent): Boolean {
        val to = intent.toClass
        if (!GetUtils.isGetActivityClass(to) && !GetUtils.isGetFragmentClass(to)) {
            ExceptionDispatcher.dispatchStarterThrowable(
                null,
                "未设置目标Class。",
                "start()跳转界面时没设置目标Class。"
            )
            return false
        }
        return true
    }

    /**
     * 获取Fragment动画
     * @param fragment 动画作用的Fragment
     * @param intent 启动传参
     */
    private fun getFragmentAnimator(fragment: Fragment, intent: GetIntent): FragmentAnimator {
        // 优先集最高的Fragment动画
        var animator = intent.fragmentAnimator
        // Fragment动画
        if (animator == null) {
            animator = (fragment as GetNavProvider).onCreateFragmentAnimator()
        }
        // Activity动画
        if (animator == null) {
            animator = (lifecycleOwner as GetNavProvider).onCreateFragmentAnimator()
        }
        // 全局默认动画
        if (animator == null) {
//            animator = Starter.getInstance().starterStrategy.fragmentAnimator
        }
        return animator?:DefaultVerticalAnimator()
    }

    private companion object {
        /** Fragment ID 关键字 **/
        const val FRAGMENT_ID_KEY = "Get:fragmentId"
    }
}