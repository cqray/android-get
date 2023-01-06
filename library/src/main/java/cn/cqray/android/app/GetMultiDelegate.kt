package cn.cqray.android.app

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.cqray.android.ui.multi.MultiFragmentAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * 多Fragment管理委托
 * @author Cqray
 */
@Suppress("unused")
class GetMultiDelegate constructor(val provider: GetMultiProvider) {

    var currentIndex = 0
        private set

    private var containerId = 0

    private var viewPager: ViewPager2? = null

    val fragments: MutableList<Fragment> = ArrayList()

    init {
        // 检查Provider是否合法
        GetUtils.checkProvider(provider)
        // 加入缓存
        cacheDelegates[provider] = this
        // 资源回收事件订阅
        (provider as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 从缓存中移除
                cacheDelegates.remove(provider)
            }
        })
    }

//    /**
//     * 加载多个Fragment
//     * @param containerId 容器Id
//     * @param intents 意图列表
//     */
//    fun loadMultiFragments(@IdRes containerId: Int, vararg intents: GetIntent?) {
//        val fragments = instantiateFragments(*intents)
//        loadMultiFragments(containerId, *fragments)
//    }
//
//    /**
//     * 加载多个Fragment
//     * @param containerId 容器Id
//     * @param fragments Fragment列表
//     */
//    fun loadMultiFragments(@IdRes containerId: Int, vararg fragments: Fragment?) {
//        loadMultiFragments(containerId, Arrays.asList(*fragments))
//    }

    /**
     * 加载多个Fragment
     * @param containerId 容器Id
     * @param fragments Fragment列表
     */
    fun loadMultiFragments(@IdRes containerId: Int, fragments: List<Fragment>) {
        this.containerId = containerId
        currentIndex = 0
        // 重置Fragment
        with(this.fragments) {
            clear()
            addAll(fragments)
        }
        val ft = fragmentManager.beginTransaction()
        for (i in fragments.indices) {
            val fragment = fragments[i]
            // 发现历史Fragment并移除
            val tag = fragments[i].javaClass.name + "-" + i
            fragmentManager.findFragmentByTag(tag)?.let { ft.remove(it) }
            // 添加新的Fragment
            ft.add(containerId, fragment, tag)
            ft.setMaxLifecycle(fragment, if (i == currentIndex) Lifecycle.State.RESUMED else Lifecycle.State.CREATED)
        }
        ft.commitAllowingStateLoss()
    }

//    /**
//     * 加载多个Fragment
//     * @param vp ViewPager2容器
//     * @param intents 意图列表
//     */
//    fun loadMultiFragments(vp: ViewPager2, vararg intents: GetIntent?) {
//        val fragments = instantiateFragments(*intents)
//        loadMultiFragments(vp, *fragments)
//    }

//    /**
//     * 加载多个Fragment
//     * @param vp ViewPager2容器
//     * @param fragments Fragment列表
//     */
//    fun loadMultiFragments(vp: ViewPager2, vararg fragments: Fragment) = loadMultiFragments(vp, fragments)

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param fragments Fragment列表
     */
    @Suppress("unused")
    fun loadMultiFragments(vp: ViewPager2, fragments: List<Fragment>) {
        viewPager = vp
        currentIndex = 0
        // 重置Fragment
        with(this.fragments) {
            clear()
            addAll(fragments)
        }
        // 已初始化过，则不需要再注册
        if (vp.adapter != null) {
            vp.adapter!!.notifyDataSetChanged()
            return
        }
        // 未初始化过，注册相关监听
        with(this.viewPager!!) {
            // 初始化Fragment适配器
            adapter = getFragmentAdapter(fragments)
            // 监听位置变化
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentIndex = position
                }
            })
            // 取消水波纹
            val child = getChildAt(0)
            (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    /**
     * 显示指定位置的Fragment
     * @param index 指定位置
     */
    fun showFragment(index: Int?) {
        val newIndex = index ?: 0
        val invalid = !(0 until fragments.size).contains(newIndex)
        if (fragments.isEmpty() || invalid) return
        when {
            // 使用的ViewPager2
            viewPager != null -> {
                viewPager?.setCurrentItem(newIndex, viewPager!!.isUserInputEnabled)
                currentIndex = newIndex
            }
            // 位置未变化
            index == currentIndex -> {
                val fragment = fragments[newIndex]
                val state = fragment!!.lifecycle.currentState
                if (!state.isAtLeast(Lifecycle.State.RESUMED)) {
                    val ft = fragmentManager.beginTransaction()
                    ft.show(fragment)
                    ft.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    ft.commitAllowingStateLoss()
                }
            }
            // 位置变化
            else -> {
                val cur = fragments[currentIndex]!!
                val to = fragments[newIndex]!!
                val ft = fragmentManager.beginTransaction()
                ft.hide(cur)
                ft.setMaxLifecycle(cur, Lifecycle.State.STARTED)
                ft.show(to)
                ft.setMaxLifecycle(to, Lifecycle.State.RESUMED)
                ft.commitAllowingStateLoss()
            }
        }
        currentIndex = newIndex
    }

    /**
     * 显示指定的Fragment
     * @param fragment 指定Fragment
     */
    fun showFragment(fragment: Fragment) = showFragment(fragments.indexOf(fragment))

    /**
     * 添加Fragment界面
     * @param cls Fragment类
     */
    fun addFragment(cls: Class<out Fragment>) {
        //addFragment(instantiateFragments(GetIntent(cls))[0])
    }


    /**
     * 添加Fragment界面
     * @param fragment Fragment
     */
    fun addFragment(fragment: Fragment) {
        if (viewPager != null) {
            val adapter = viewPager!!.adapter as MultiFragmentAdapter
            adapter.fragmentList.add(fragment)
            adapter.notifyItemInserted(adapter.fragmentList.size - 1)
        } else {
            fragments.add(fragment)
            val ft = fragmentManager.beginTransaction()
            ft.add(containerId, fragment)
            ft.setMaxLifecycle(fragment, Lifecycle.State.CREATED)
            ft.commitAllowingStateLoss()
        }
    }

    /**
     * 移除指定的Fragment界面
     * @param index 位置
     */
    fun removeFragment(index: Int?) {
        index?.let {
            val valid = (0 until fragments.size).contains(it)
            if (valid) removeFragment(fragments[it]!!)
        }
    }

    /**
     * 移除Fragment界面
     * @param fragment Fragment
     */
    fun removeFragment(fragment: Fragment) {
        val index = fragments.indexOf(fragment)
        if (index < 0) return
        if (viewPager != null) {
            // 从ViewPager2中移除Fragment
            val adapter = viewPager!!.adapter as MultiFragmentAdapter
            adapter.fragmentList.remove(fragment)
            adapter.notifyItemRemoved(index)
        } else {
            // 从列表中移除Fragment
            fragments.remove(fragment)
            // 从栈中移除Fragment
            val ft = fragmentManager.beginTransaction()
            ft.remove(fragment)
            ft.commitAllowingStateLoss()
        }
        // 显示新位置的Fragment
        val newIndex = if (index == 0) 0 else index - 1
        showFragment(newIndex)
    }

    fun reset() {
        for (fragment in fragments) {
            val fm = fragmentManager
            val ft = fm.beginTransaction()
            ft.remove(fragment!!)
            ft.commitAllowingStateLoss()
        }
        currentIndex = 0
        fragments.clear()
        viewPager = null
    }

    @Suppress("unused")
    val fragmentManager: FragmentManager
        get() = if (provider is FragmentActivity) {
            provider.supportFragmentManager
        } else (provider as Fragment).childFragmentManager

//    val fragments: List<Fragment?>
//        get() = fragments
    
    val activity: FragmentActivity?
        get() = if (provider is FragmentActivity) {
            provider
        } else (provider as Fragment).activity

    fun requireActivity(): FragmentActivity {
        return if (provider is FragmentActivity) {
            provider
        } else (provider as Fragment).requireActivity()
    }

    private fun getFragmentAdapter(fragmentList: List<Fragment?>): MultiFragmentAdapter {
        return if (provider is FragmentActivity) {
            MultiFragmentAdapter(provider, fragmentList)
        } else {
            MultiFragmentAdapter((provider as Fragment), fragmentList)
        }
    }

    /**
     * 生成Fragment列表
     * @param intents 意图列表
     */
    private fun instantiateFragments(vararg intents: GetIntent): Array<Fragment?> {
        val fragments = arrayOfNulls<Fragment>(intents.size)
        for (i in fragments.indices) {
            val className = intents[i].toClass!!.name
            val fragmentFactory = fragmentManager.fragmentFactory
            fragments[i] = fragmentFactory.instantiate(requireActivity().classLoader, className)
            fragments[i]!!.arguments = intents[i].arguments
        }
        return fragments
    }

//    init {
//        fragments = Collections.synchronizedList(ArrayList())
//    }

    companion object {

        /** 委托缓存 [GetMultiDelegate] **/
        private val cacheDelegates = HashMap<GetMultiProvider, GetMultiDelegate>()

        /**
         * 获取并初始化[GetMultiDelegate]
         * @param provider [GetNavProvider]实现实例
         */
        @JvmStatic
        @Synchronized
        fun get(provider: GetMultiProvider): GetMultiDelegate =
            cacheDelegates[provider] ?: GetMultiDelegate(provider)
    }
}