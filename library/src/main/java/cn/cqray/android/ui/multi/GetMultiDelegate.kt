package cn.cqray.android.ui.multi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetDelegate
import cn.cqray.android.log.GetLog
import cn.cqray.android.util.Contexts
import cn.cqray.android.util.Views

/**
 * 多Fragment委托
 * @author Cqray
 */
@Suppress("MemberVisibilityCanBePrivate")
class GetMultiDelegate(
    provider: GetMultiProvider
) : GetDelegate<GetMultiProvider>(provider) {

    /** [ViewPager2]回调 **/
    private val vpCallbacks = HashMap<ViewPager2, ViewPager2.OnPageChangeCallback>()

    /**
     * 获取对应[ViewPager2]容器下的Fragment列表
     * @param vp 容器
     */
    fun getFragments(vp: ViewPager2) = vp.let {
        initViewPager(it)
        (vp.adapter as GetMultiFragmentAdapter).fragments
    }

    /**
     * 生成Fragment列表
     * @param target 目标Fragment
     * @param arguments 参数
     */
    fun instantiateFragment(target: Class<out Fragment>, arguments: Bundle? = null): Fragment {
        val fragment = target.newInstance()
        fragment.arguments = arguments ?: Bundle()
        return fragment
    }

    /**
     * 获取[ViewPager2]容器名称
     * @param vp [ViewPager2]
     */
    private fun getContainerName(vp: ViewPager2) = vp.let {
        val name = Contexts.getIdName(vp.id)
        if (name.isEmpty()) "ViewPager2"
        else "ViewPager2[$name]"
    }

    /**
     * 给[ViewPager2]注册适配器
     * @param vp 容器
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun registerFragmentAdapter(vp: ViewPager2) {
        // 获取适配器
        val oldAdapter = vp.adapter as? GetMultiFragmentAdapter
        // 适配器为空则说明未注册
        if (oldAdapter == null) {
            val adapter = if (provider is FragmentActivity) {
                GetMultiFragmentAdapter(provider, mutableListOf())
            } else {
                GetMultiFragmentAdapter((provider as Fragment), mutableListOf())
            }
            vp.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * 给[ViewPager2]注册监听事件
     * @param vp 容器
     */
    private fun registerOnPageChangeCallback(vp: ViewPager2) {
        // 从缓存中获取回调
        val cacheCallback = vpCallbacks[vp]
        // 不存在回调说明未注册
        if (cacheCallback == null) {
            // 初始化回调函数
            val callback = object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    provider.onFragmentPageSelected(vp, position)
                    // 打印日志
                    printLog(
                        GetLog.D,
                        "onPageSelected",
                        "Index has changed to [$position - ${
                            getFragments(vp)[position].javaClass.simpleName
                        }] in ${getContainerName(vp)}."
                    )
                }
            }
            // 注册监听
            vp.registerOnPageChangeCallback(callback)
            // 存入缓存
            vpCallbacks[vp] = callback
        }
    }

    /**
     * 初始化[ViewPager2]容器
     * @param vp 容器
     */
    private fun initViewPager(vp: ViewPager2) {
        // 转屏销毁后，不保存数据
        vp.isSaveEnabled = false
        // 注册适配器
        registerFragmentAdapter(vp)
        // 注册回调监听
        registerOnPageChangeCallback(vp)
        // 获取内置RecyclerView
        val recyclerView = (vp.getChildAt(0) as? RecyclerView)
        // 关闭水波纹
        recyclerView?.overScrollMode = View.OVER_SCROLL_NEVER
        // 关闭动画
        Views.closeRvAnimator(recyclerView)
    }

    /**
     * 加载多个Fragment
     * @param vp ViewPager2容器
     * @param fragments Fragment列表
     */
    @Suppress("NotifyDataSetChanged")
    fun loadMultiFragments(vp: ViewPager2, fragments: Array<Fragment>) {
        // 初始化Fragment列表
        val list = getFragments(vp).also {
            it.clear()
            it.addAll(fragments.toList())
        }
        // 更新数据
        vp.adapter?.notifyDataSetChanged()
        // 因为列表可能为空，所以要runCatching
        runCatching {
            // 根据新的Fragment列表生成新的有效的索引
            val index = vp.currentItem.let {
                if (it !in 0 until list.size) 0
                else it
            }
            // 设置当前项
            vp.setCurrentItem(index, vp.isUserInputEnabled)
        }
        // 打印日志
        printLog(
            GetLog.D,
            "loadMultiFragments",
            "[${list.size}] fragments has been loaded in ${getContainerName(vp)}."
        )
    }

    /**
     * 显示指定容器指定位置的[Fragment]
     * @param vp [ViewPager2]容器
     * @param index 指定位置
     */
    fun showFragment(vp: ViewPager2, index: Int) {
        // 对应Fragment列表
        val list = getFragments(vp)
        // 索引判断
        index.let {
            if (!(0 until list.size).contains(it)) {
                printLog(
                    GetLog.W,
                    "showFragment",
                    "Index [$index] is invalid, there will do thing."
                )
                return
            }
        }
        // 设置当前项
        vp.setCurrentItem(index, vp.isUserInputEnabled)
        // 打印日志
        printLog(
            GetLog.D,
            "showFragment",
            "Index has changed to [$index - ${list[index].javaClass.simpleName}] in ${getContainerName(vp)}."
        )
    }

    /**
     * 显示指定容器指定的Fragment
     * @param vp [ViewPager2]容器
     * @param fragment 指定[Fragment]
     */
    fun showFragment(vp: ViewPager2, fragment: Fragment) {
        val list = getFragments(vp)
        val index = list.indexOf(fragment)
        if (index >= 0) showFragment(vp, index)
        else printLog(
            GetLog.W,
            "showFragment",
            "[${fragment.javaClass.simpleName}] can't be find in ${getContainerName(vp)}."
        )
    }

    fun addFragment(vp: ViewPager2, fragment: Fragment, index: Int?) {
        // 对应Fragment列表
        val list = getFragments(vp)
        // 新索引
        val newIndex = (index ?: list.size)
        // 索引判断
        newIndex.let {
            if (it !in (0..list.size)) {
                printLog(
                    GetLog.W,
                    "addFragment",
                    "Index [$index] is invalid, there will do thing."
                )
                return
            }
        }
        // 添加Fragment
        (vp.adapter as GetMultiFragmentAdapter).addFragment(fragment, newIndex)
        // 打印日志
        printLog(
            GetLog.D,
            "addFragment",
            "[${fragment.javaClass.simpleName}] has added at [$newIndex] in ${getContainerName(vp)}"
        )
    }

    /**
     * 移除[ViewPager2]容器中指定Fragment
     * @param vp 容器
     * @param fragment 指定Fragment
     */
    fun removeFragment(vp: ViewPager2, fragment: Fragment) {
        val list = getFragments(vp)
        val index = list.indexOf(fragment)
        if (index >= 0) removeFragment(vp, index)
        else printLog(
            GetLog.W,
            "removeFragment",
            "[${fragment.javaClass.simpleName}] can't be find in ${getContainerName(vp)}."
        )
    }

    /**
     * 移除[ViewPager2]容器中指定位置的Fragment
     * @param vp 容器
     * @param index 指定位置
     */
    fun removeFragment(vp: ViewPager2, index: Int) {
        // 对应Fragment列表
        val list = getFragments(vp)
        // 索引判断
        index.let {
            if (it !in list.indices) {
                printLog(
                    GetLog.W,
                    "removeFragment",
                    "Index [$index] is invalid, there will do thing."
                )
                return
            }
        }
        // 指定Fragment
        val fragment = list[index]
        // 获取改变后的位置
        val changedIndex = if (index == 0) 0 else index - 1
        // 移除Fragment
        (vp.adapter as GetMultiFragmentAdapter).removeFragment(index)
        // 打印日志
        printLog(
            GetLog.D,
            "removeFragment",
            "[${fragment.javaClass.simpleName}] has removed at [$index] in ${getContainerName(vp)}"
        )
        // 显示新的Fragment
        showFragment(vp, changedIndex)
    }

    /**
     * 移除容器所有Fragment
     * @param vp 容器
     */
    @SuppressLint("NotifyDataSetChanged")
    fun removeFragments(vp: ViewPager2) {
        // 清空Fragment列表
        getFragments(vp).clear()
        // 更新数据
        vp.adapter?.notifyDataSetChanged()
    }

}