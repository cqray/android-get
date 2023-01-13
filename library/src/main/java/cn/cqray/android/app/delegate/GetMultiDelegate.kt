package cn.cqray.android.app.delegate

import android.os.Bundle
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle.State
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.cqray.android.Get
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.ui.multi.GetMultiFragmentAdapter
import cn.cqray.android.log.LogLevel
import cn.cqray.android.util.ContextUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * 多[Fragment]管理委托
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class GetMultiDelegate constructor(provider: GetMultiProvider) :
    GetDelegate<GetMultiProvider>(provider) {

    /** 容器是[ViewPager2] **/
    private var viewPager: ViewPager2? = null

    /** 不同容器下，索引缓存 **/
    private val indexCache = SparseIntArray()

    /** 不同容器下Fragment缓存  **/
    private val fragmentCache = SparseArray<MutableList<Fragment>>()

    /** [FragmentManager]实例 **/
    private val fragmentManager: FragmentManager
        get() = if (provider is FragmentActivity) {
            provider.supportFragmentManager
        } else (provider as Fragment).childFragmentManager

    /** [ViewPager2]下的[Fragment]列表 **/
    val fragments: MutableList<Fragment> get() = getFragments(View.NO_ID)

    /** [ViewPager2]下的当前索引 **/
    val currentIndex: Int get() = getCurrentIndex(View.NO_ID)

    /**
     * 获取指定容器的[Fragment]列表
     * @param containerId 指定容器ID
     */
    fun getFragments(@IdRes containerId: Int?): MutableList<Fragment> {
        val newId = containerId ?: View.NO_ID
        // 获取并初始化对应容器的Fragment列表
        return fragmentCache.get(newId) ?: newId.let {
            val list = ArrayList<Fragment>()
            fragmentCache.put(newId, list)
            list
        }
    }

    /**
     * 获取指定容器的索引位置
     * @param containerId 指定容器ID
     */
    fun getCurrentIndex(@IdRes containerId: Int?): Int {
        val newId = containerId ?: View.NO_ID
        return indexCache.get(newId)
    }

    /**
     * 生成Fragment适配器[GetMultiFragmentAdapter]
     */
    fun getFragmentAdapter(fragmentList: List<Fragment>): GetMultiFragmentAdapter {
        return if (provider is FragmentActivity) {
            GetMultiFragmentAdapter(provider, fragmentList)
        } else {
            GetMultiFragmentAdapter((provider as Fragment), fragmentList)
        }
    }

    /**
     * 生成Fragment列表
     * @param target 目标Fragment
     * @param arguments 参数
     */
    fun instantiateFragment(target: Class<out Fragment>, arguments: Bundle? = null): Fragment {
        val name = target.name
        val classLoader = Get.context.classLoader
        val fragmentFactory = fragmentManager.fragmentFactory
        val fragment = fragmentFactory.instantiate(classLoader, name)
        fragment.arguments = arguments ?: Bundle()
        return fragment
    }

    /**
     * 在指定容器中，加载多个Fragment
     * @param containerId 容器Id
     * @param fragments Fragment列表
     */
    fun loadMultiFragments(@IdRes containerId: Int?, fragments: Array<Fragment>) {
        // 列表为空，则不继续处理
        if (containerId == null) {
            throw RuntimeException("The container id can't be null.")
        }
        // 获取并初始化对应容器的Fragment列表
        val list = getFragments(containerId)
        // 根据新的Fragment列表生成新的有效的索引
        val index = changeIndex(containerId, fragments)
        // 开启事务
        val ft = fragmentManager.beginTransaction()
        // 遍历移除旧的Fragment
        list.forEach { ft.remove(it) }
        // 重置列表
        list.clear().also { list.addAll(fragments) }
        // 缓存新的列表
        fragmentCache.put(containerId, list)
        // 遍历添加新的Fragment
        fragments.forEachIndexed { i, fragment ->
            // 获取Fragment的标识（对应不同的容器）
            val tag = containerId.toString() + "-" + fragment.javaClass.name + "-" + i
            // 移除已存在标识的相同Fragment
            fragmentManager.findFragmentByTag(tag)?.let { ft.remove(it) }
            // 添加新的Fragment
            ft.add(containerId, fragment, tag)
            // 获取最大初始化所在生命周期状态
            val maxLifecycle = if (i == index) State.RESUMED else State.CREATED
            ft.setMaxLifecycle(fragment, maxLifecycle)
        }
        // 提交事务
        ft.commitAllowingStateLoss()
        // 打印日志
        printLog(
            LogLevel.D,
            "loadMultiFragments",
            "[${fragments.size}] fragments has been loaded in container[${ContextUtils.getIdName(containerId)}]."
        )
    }

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param fragments Fragment列表
     */
    @Suppress("NotifyDataSetChanged")
    fun loadMultiFragments(vp: ViewPager2, fragments: Array<Fragment>) {
        // 获取并初始化对应容器的Fragment列表
        val list = getFragments(View.NO_ID)
        // 根据新的Fragment列表生成新的有效的索引
        val index = changeIndex(View.NO_ID, fragments)
        // 重置列表
        list.clear().also { list.addAll(fragments) }
        // 缓存Fragment
        fragmentCache.put(View.NO_ID, list)
        // 匿名打印日志函数
        val printLog = {
            printLog(
                LogLevel.D,
                "loadMultiFragments",
                "[${fragments.size}] fragments has been loaded in ViewPager2."
            )
        }
        // 设置ViewPager2
        viewPager = vp
        // 已初始化过，则不需要再注册
        vp.adapter?.let {
            // 更新数据
            it.notifyDataSetChanged()
            // 设置当前项
            vp.setCurrentItem(index, vp.isUserInputEnabled)
            // 打印日志
            printLog()
            return
        }
        // 未初始化过，注册相关监听
        with(vp) {
            // 初始化Fragment适配器
            adapter = getFragmentAdapter(list)
            // 监听位置变化
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 缓存索引
                    indexCache.put(View.NO_ID, position)
                    // 获取ViewPager2缓存的Fragment
                    val temp = this@GetMultiDelegate.fragments
                    // 打印日志
                    printLog(
                        LogLevel.D,
                        "onPageSelected",
                        "Index has changed to [$position - ${temp[position]::class.java.simpleName}] in ViewPager2."
                    )
                }
            })
            // 设置当前项
            setCurrentItem(index, vp.isUserInputEnabled)
            // 取消水波纹
            val recyclerView = getChildAt(0) as? RecyclerView
            recyclerView?.overScrollMode = View.OVER_SCROLL_NEVER
        }
        // 打印日志
        printLog()
    }

    /**
     * 显示指定容器指定位置的[Fragment]
     * @param containerId   指定容器ID
     * @param index         指定位置
     */
    fun showFragment(@IdRes containerId: Int?, index: Int?) {
        // 获取新的ID
        val newId = containerId ?: View.NO_ID
        // 获取Fragment列表
        val fragments = getFragments(newId)
        // 索引判断
        (index ?: -1).let {
            if (!(0 until fragments.size).contains(it)) {
                printLog(
                    LogLevel.W,
                    "showFragment",
                    "Index [$index] is invalid, there will do thing."
                )
                return
            }
        }
        // 匿名打印日志函数
        val printLog = { i: Int ->
            printLog(
                LogLevel.D,
                "showFragment",
                "Index has changed to [$index - ${fragments[i]::class.java.simpleName}] in ${
                    if ((containerId ?: View.NO_ID) == View.NO_ID) "ViewPager2."
                    else "container[${ContextUtils.getIdName(containerId)}]."
                }"
            )
        }
        // 新索引
        val newIndex = index!!
        // 使用[ViewPager2]时，其为空，则不继续处理
        if (newId == View.NO_ID) {
            viewPager?.let {
                it.setCurrentItem(newIndex, it.isUserInputEnabled)
                indexCache.put(View.NO_ID, newIndex)
                printLog(newIndex)
            }
            return
        }
        // 处理是普通容器的情况
        // 当前Fragment
        val current = fragments[getCurrentIndex(newId)]
        // 目标Fragment
        val target = fragments[newIndex]
        // 开启事务
        val ft = fragmentManager.beginTransaction()
        // 切换不同的Fragment时，隐藏当前Fragment，并切换生命周期
        if (current != target) {
            ft.hide(current)
            ft.setMaxLifecycle(current, State.STARTED)
        }
        // 目标Fragment的生命周期状态不是RESUMED时
        // 提交切换Fragment的事务，并切换生命周期
        val state = target.lifecycle.currentState
        if (!state.isAtLeast(State.RESUMED)) {
            ft.show(target)
            ft.setMaxLifecycle(target, State.RESUMED)
            ft.commitAllowingStateLoss()
            indexCache.put(newId, newIndex)
            printLog(newIndex)
        }
    }

    /**
     * 显示指定容器指定的Fragment
     * @param containerId 指定容器ID
     * @param fragment 指定[Fragment]
     */
    fun showFragment(@IdRes containerId: Int?, fragment: Fragment) {
        val fragments = getFragments(containerId)
        val index = fragments.indexOf(fragment)
        if (index >= 0) showFragment(containerId, index)
        else printLog(
            LogLevel.W,
            "showFragment",
            "[${fragment::class.java.simpleName}] can't be find in ${
                if ((containerId ?: View.NO_ID) == View.NO_ID) "ViewPager2."
                else "container[${ContextUtils.getIdName(containerId)}]."
            }"
        )
    }

    /**
     * 添加Fragment界面
     * @param containerId 容器ID
     * @param fragment Fragment
     */
    @Suppress("NotifyDataSetChanged")
    fun addFragment(@IdRes containerId: Int?, fragment: Fragment, index: Int?) {
        val newId = containerId ?: View.NO_ID
        // 获取Fragment列表
        val fragments = getFragments(newId)
        // 新的位置
        val newIndex = (index ?: fragments.size).let {
            if (it < 0) 0
            else if (it > fragments.size - 1) fragments.size
            else it
        }
        // 匿名打印日志函数
        val printLog = { i: Int ->
            printLog(
                LogLevel.D,
                "addFragment",
                "[${fragment::class.java.simpleName}] has added at [$i] in ${
                    if (newId == View.NO_ID) "ViewPager2."
                    else "container[${ContextUtils.getIdName(containerId)}]."
                }"
            )
        }
        // 存入缓存
        fragments.add(newIndex, fragment)
        fragmentCache.put(newId, fragments)
        // 处理容器是ViewPager2的情况
        if (newId == View.NO_ID) {
            viewPager?.let {
                val adapter = it.adapter as? GetMultiFragmentAdapter
                    ?: getFragmentAdapter(fragments).also { o -> it.adapter = o }
                adapter.fragments.clear()
                adapter.fragments.addAll(fragments)
                adapter.notifyDataSetChanged()
                // 打印日志
                printLog(newIndex)
                // 显示新位置
                showFragment(View.NO_ID, newIndex)
            }
            return
        }
        // 提交添加Fragment事务
        val ft = fragmentManager.beginTransaction()
        ft.add(newId, fragment)
        ft.setMaxLifecycle(fragment, State.CREATED)
        ft.commitAllowingStateLoss()
        // 打印日志
        printLog(newIndex)
        // 显示新位置
        showFragment(containerId, newIndex)
    }

    /**
     * 移除指定的Fragment
     * @param index 位置
     */
    fun removeFragment(@IdRes containerId: Int?, index: Int?) {
        // 获取新的ID
        val newId = containerId ?: View.NO_ID
        // 获取Fragment列表
        val fragments = getFragments(newId)
        // 索引判断
        (index ?: -1).let {
            if (!(0 until fragments.size).contains(it)) {
                printLog(
                    LogLevel.W,
                    "removeFragment",
                    "Index [$index] is invalid, there were do thing."
                )
                return
            }
        }
        // 匿名打印日志函数
        val printLog = { i: Int ->
            printLog(
                LogLevel.D,
                "removeFragment",
                "[$i - ${fragments[i]::class.java.simpleName}] has removed in ${
                    if (newId == View.NO_ID) "ViewPager2."
                    else "container[${ContextUtils.getIdName(containerId)}]."
                }"
            )
        }
        // 非null
        val newIndex = index!!
        // 获取改变后的位置
        val changedIndex = if (newIndex == 0) 0 else newIndex - 1
        // 要移除的Fragment
        val fragment = fragments[newIndex]
        // 处理容器是ViewPager2的情况
        viewPager?.let {
            // 从ViewPager2中移除Fragment
            val adapter = viewPager!!.adapter as? GetMultiFragmentAdapter
            adapter?.let {
                // 移除Fragment
                it.fragments.remove(fragment)
                it.notifyItemRemoved(newIndex)
                // 打印日志
                printLog(newIndex)
                // 显示新位置的Fragment
                showFragment(containerId, changedIndex)
            }
            return
        }
        // 处理是普通容器的情况
        // 从列表中移除Fragment
        fragments.remove(fragment)
        // 从栈中移除Fragment
        val ft = fragmentManager.beginTransaction()
        ft.remove(fragment)
        ft.commitAllowingStateLoss()
        // 打印日志
        printLog(newIndex)
        // 显示新位置的Fragment
        showFragment(containerId, changedIndex)
    }

    /**
     * 移除指定的Fragment
     * @param fragment Fragment界面
     */
    fun removeFragment(@IdRes containerId: Int?, fragment: Fragment) {
        val fragments = getFragments(containerId)
        val index = fragments.indexOf(fragment)
        if (index >= 0) removeFragment(containerId, index)
        else printLog(
            LogLevel.W,
            "removeFragment",
            "[${fragment::class.java.simpleName}] can't be find in ${
                if ((containerId ?: View.NO_ID) == View.NO_ID) "ViewPager2."
                else "container[${ContextUtils.getIdName(containerId)}]"
            }"
        )
    }

    /**
     * 移除指定容器下所有的Fragment
     * @param containerId 指定容器ID，null时代表ViewPager
     */
    fun removeFragments(@IdRes containerId: Int? = null) {
        val newId = containerId ?: View.NO_ID
        // 处理容器是ViewPager2的情况
        if (newId == View.NO_ID) {
            // 清除ViewPager2中的Fragment
            removeViewPagerFragments()
            return
        }
        // 处理是普通容器的情况
        // 移除所有Fragment
        val ft = fragmentManager.beginTransaction()
        val fragments = getFragments(newId)
        fragments.forEach { ft.remove(it) }
        fragments.clear()
        fragmentCache.remove(newId)
        ft.commitAllowingStateLoss()
        // 打印日志
        printLog(
            LogLevel.D,
            "removeFragments",
            "All fragments has been removed in container[${ContextUtils.getIdName(containerId)}]."
        )
    }

    /**
     * 移除所有[Fragment]
     */
    fun removeAllFragments() {
        // 清除ViewPager2中的Fragment
        removeViewPagerFragments()
        // 清理其他容器的Fragment
        for (i in 0 until fragmentCache.size()) {
            val id = fragmentCache.keyAt(i)
            removeFragments(id)
        }
        // 打印日志
        printLog(LogLevel.D, "removeFragments", "All fragments has been removed.")
    }

    /**
     * 移除[ViewPager2]容器下所有的Fragment
     */
    @Suppress("NotifyDataSetChanged")
    private fun removeViewPagerFragments() {
        viewPager?.let {
            // 从缓存中移除
            fragments.clear()
            fragmentCache.remove(View.NO_ID)
            // 更新Adapter
            (it.adapter as? GetMultiFragmentAdapter)?.let { adapter ->
                adapter.fragments.clear()
                adapter.notifyDataSetChanged()
            }
            // 打印日志
            printLog(LogLevel.D, "removeFragments", "All fragments has been removed in ViewPager2.")
        }
    }

    /**
     * 切换指定容器的Index
     * @param containerId 指定容器
     * @param fragments Fragment列表
     */
    private fun changeIndex(@IdRes containerId: Int?, fragments: Array<Fragment>): Int {
        val newId = containerId ?: View.NO_ID
        val index = newId.let {
            // 获取指定容器的Index
            val index = getCurrentIndex(it)
            // 获取有效的Index
            val array = fragments.indices
            if (array.contains(index)) index else 0
        }
        indexCache.put(newId, index)
        return index
    }
}