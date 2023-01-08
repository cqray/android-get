package cn.cqray.android.app.delegate

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
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.ui.multi.MultiFragmentAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * 多Fragment管理委托
 * @author Cqray
 */
@Suppress("unused")
class GetMultiDelegate constructor(provider: GetMultiProvider) :
    GetDelegate<GetMultiProvider>(provider) {

    /** 容器是[ViewPager2] **/
    private var viewPager: ViewPager2? = null

    /** 索引位置（不同容器）列表 **/
    private val indexArray = SparseIntArray()

    /** [Fragment]（不同容器）列表  **/
    private val fragmentArray = SparseArray<MutableList<Fragment>>()

    /** [FragmentManager]实例 **/
    private val fragmentManager: FragmentManager
        get() = if (provider is FragmentActivity) {
            provider.supportFragmentManager
        } else (provider as Fragment).childFragmentManager

    /** [ViewPager2]下的[Fragment]列表 **/
    val fragments = getFragments(View.NO_ID)

    /** [ViewPager2]下的当前索引 **/
    val currentIndex = getCurrentIndex(View.NO_ID)

    /**
     * 获取指定容器的[Fragment]列表
     * @param containerId 指定容器ID
     */
    @Suppress
    @Synchronized
    fun getFragments(@IdRes containerId: Int?): MutableList<Fragment> {
        val newId = containerId ?: View.NO_ID
        // 获取并初始化对应容器的Fragment列表
        return fragmentArray.get(newId)?.let {
            val list = ArrayList<Fragment>()
            fragmentArray.put(newId, list)
            list
        }!!
    }

    /**
     * 获取指定容器的索引位置
     * @param containerId 指定容器ID
     */
    @Suppress("unused")
    fun getCurrentIndex(@IdRes containerId: Int?): Int {
        val newId = containerId ?: View.NO_ID
        return indexArray.get(newId)
    }

    /**
     * 在指定容器中，加载多个Fragment
     * @param containerId 容器Id
     * @param fragments Fragment列表
     */
    fun loadMultiFragments(@IdRes containerId: Int?, fragments: List<Fragment>) {
        // 列表为空，则不继续处理
        if (fragments.isEmpty()) return
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
        fragmentArray.put(containerId, list)
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
    }

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param fragments Fragment列表
     */
    @Suppress("unused")
    fun loadMultiFragments(vp: ViewPager2, fragments: List<Fragment>) {
        // 列表为空，则不继续处理
        if (fragments.isEmpty()) return
        // 获取并初始化对应容器的Fragment列表
        val list = getFragments(View.NO_ID)
        // 根据新的Fragment列表生成新的有效的索引
        val index = changeIndex(View.NO_ID, fragments)
        // 重置列表
        list.clear().also { list.addAll(fragments) }
        // 缓存Fragment
        fragmentArray.put(View.NO_ID, list)
        // 设置ViewPager2
        viewPager = vp
        // 已初始化过，则不需要再注册
        vp.adapter?.let {
            @Suppress("NotifyDataSetChanged")
            it.notifyDataSetChanged()
            // 设置当前项
            vp.setCurrentItem(index, vp.isUserInputEnabled)
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
                    indexArray.put(View.NO_ID, position)
                }
            })
            // 设置当前项
            setCurrentItem(index, vp.isUserInputEnabled)
            // 取消水波纹
            val recyclerView = getChildAt(0) as? RecyclerView
            recyclerView?.overScrollMode = View.OVER_SCROLL_NEVER
        }
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
        // 判断是否是有效的位置
        val newIndex = index ?: 0
        // 队列为空不继续
        if (fragments.isEmpty()) return
        // 无效Index不继续
        if (!(0 until fragments.size).contains(newIndex)) return
        // 使用[ViewPager2]时，其为空，则不继续处理
        if (newId == View.NO_ID) {
            viewPager?.let {
                it.setCurrentItem(newIndex, it.isUserInputEnabled)
                indexArray.put(View.NO_ID, newIndex)
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
            indexArray.put(newId, newIndex)
        }
    }

    /**
     * 显示指定容器指定的[Fragment]
     * @param containerId   指定容器ID
     * @param fragment      指定[Fragment]
     */
    fun showFragment(@IdRes containerId: Int?, fragment: Fragment) {
        val fragments = getFragments(containerId)
        showFragment(containerId, fragments.indexOf(fragment))
    }

//    /**
//     * 添加Fragment界面
//     * @param cls Fragment类
//     */
//    fun addFragment(cls: Class<out Fragment>) {
//        //addFragment(instantiateFragments(GetIntent(cls))[0])
//    }


    /**
     * 添加Fragment界面
     * @param fragment Fragment
     */
    fun addFragment(@IdRes containerId: Int?, fragment: Fragment) {
        val newId = containerId ?: View.NO_ID
        // 获取Fragment列表
        val fragments = getFragments(newId).also { it.add(fragment) }
        // 存入缓存
        fragmentArray.put(newId, fragments)
        // 处理容器是ViewPager2的情况
        if (newId == View.NO_ID) {
            viewPager?.let {
                val adapter = it.adapter as MultiFragmentAdapter
                adapter.fragmentList.add(fragment)
                adapter.notifyItemInserted(adapter.fragmentList.size - 1)
            }
            return
        }
        // 提交添加Fragment事务
        val ft = fragmentManager.beginTransaction()
        ft.add(newId, fragment)
        ft.setMaxLifecycle(fragment, State.CREATED)
        ft.commitAllowingStateLoss()
    }

    /**
     * 移除指定的[Fragment]
     * @param index 位置
     */
    fun removeFragment(@IdRes containerId: Int?, index: Int?) {
        // 获取新的ID
        val newId = containerId ?: View.NO_ID
        // 获取Fragment列表
        val fragments = getFragments(newId)
        // 判断是否是有效的位置
        val newIndex = index ?: 0
        // 队列为空不继续
        if (fragments.isEmpty()) return
        // 无效Index不继续
        if (!(0 until fragments.size).contains(newIndex)) return
        // 获取改变后的位置
        val changedIndex = if (newIndex == 0) 0 else newIndex - 1
        // 要移除的Fragment
        val fragment = fragments[newIndex]
        // 处理容器是ViewPager2的情况
        viewPager?.let {
            // 从ViewPager2中移除Fragment
            val adapter = viewPager!!.adapter as MultiFragmentAdapter
            adapter.fragmentList.remove(fragment)
            adapter.notifyItemRemoved(newIndex)
            // 显示新位置的Fragment
            showFragment(containerId, changedIndex)
            return
        }
        // 处理是普通容器的情况
        // 从列表中移除Fragment
        fragments.remove(fragment)
        // 从栈中移除Fragment
        val ft = fragmentManager.beginTransaction()
        ft.remove(fragment)
        ft.commitAllowingStateLoss()
        // 显示新位置的Fragment
        showFragment(containerId, changedIndex)
    }

    /**
     * 移除指定的[Fragment]
     * @param fragment [Fragment]
     */
    fun removeFragment(@IdRes containerId: Int?, fragment: Fragment) {
        val fragments = getFragments(containerId)
        removeFragment(containerId, fragments.indexOf(fragment))
    }

    /**
     * 移除指定容器下所有的[Fragment]
     * @param containerId 指定容器ID
     */
    fun removeFragments(@IdRes containerId: Int?) {
        val newId = containerId ?: View.NO_ID
        // 处理容器是ViewPager2的情况
        if (newId == View.NO_ID) {
            // 清除ViewPager2中的Fragment
            removeVpFragments()
            return
        }
        // 处理是普通容器的情况
        // 移除所有Fragment
        val ft = fragmentManager.beginTransaction()
        val fragments = getFragments(newId)
        fragments.forEach { ft.remove(it) }
        fragments.clear()
        fragmentArray.remove(newId)
        ft.commitAllowingStateLoss()
    }

    /**
     * 移除所有[Fragment]
     */
    fun removeAllFragments() {
        // 清除ViewPager2中的Fragment
        removeVpFragments()
        // 清理其他容器的Fragment
        val ft = fragmentManager.beginTransaction()
        for (i in 0 until fragmentArray.size()) {
            val fragments = fragmentArray.valueAt(i)
            fragments.forEach { ft.remove(it) }
            fragments.clear()
        }
        // 清除所有Fragment
        fragmentArray.clear()
        // 提交事务
        ft.commitAllowingStateLoss()
    }

    /**
     * 移除[ViewPager2]容器下所有的[Fragment]
     */
    private fun removeVpFragments() {
        viewPager?.let {
            // 从缓存中移除
            fragments.clear()
            fragmentArray.remove(View.NO_ID)
            // 更新Adapter
            @Suppress("NotifyDataSetChanged")
            it.adapter?.notifyDataSetChanged()
        }
    }

    /**
     * 切换指定容器的Index
     * @param containerId 指定容器
     * @param fragments [Fragment]列表
     */
    private fun changeIndex(@IdRes containerId: Int?, fragments: List<Fragment>): Int {
        val newId = containerId ?: View.NO_ID
        val index = newId.let {
            // 获取指定容器的Index
            val index = getCurrentIndex(it)
            // 获取有效的Index
            val array = fragments.indices
            if (array.contains(index)) index else 0
        }
        return index
    }
    
    /**
     * 生成[Fragment]适配器[MultiFragmentAdapter]
     */
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
            fragments[i] = fragmentFactory.instantiate(Get.context.classLoader, className)
            fragments[i]!!.arguments = intents[i].arguments
        }
        return fragments
    }
}