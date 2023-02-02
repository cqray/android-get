package cn.cqray.android.multi

import android.annotation.SuppressLint
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.log.LogLevel
import cn.cqray.android.util.ContextUtils

@Suppress("unused", "MemberVisibilityCanBePrivate")
class GetMultiViewModel3(lifecycleOwner: LifecycleOwner) : GetViewModel(lifecycleOwner) {

    /** Fragment当前位置索引（根据容器ID区分） **/
    val indexArrays = SparseIntArray()

    /** Fragment集合（根据容器ID区分） **/
    val fragmentArrays = SparseArray<MutableList<Fragment>>()


//    val fragments get() = fragmentArrays.get(View.NO_ID)

    val callbackArrays = SparseArray<ViewPager2.OnPageChangeCallback>()

    /** [FragmentManager]实例 **/
    val fragmentManager: FragmentManager
        get() = if (lifecycleOwner is FragmentActivity) {
            lifecycleOwner.supportFragmentManager
        } else (lifecycleOwner as Fragment).childFragmentManager

    /** 多Fragment管理委托 **/
    val multiDelegate: GetMultiDelegate2? by lazy { (lifecycleOwner as? GetMultiProvider)?.multiDelegate2 }

    /**
     * 获取对应[ViewPager2]容器下的Fragment列表
     * @param vp 容器
     */
    fun getFragments(vp: ViewPager2) = vp.let {
        initViewPager(it)
        (vp.adapter as GetMultiFragmentAdapter).fragments
    }

//    /**
//     * 生成Fragment列表
//     * @param target 目标Fragment
//     * @param arguments 参数
//     */
//    fun instantiateFragment(target: Class<out Fragment>, arguments: Bundle? = null): Fragment {
//        val name = target.name
//        val classLoader = Get.context.classLoader
//        val fragmentFactory = fragmentManager.fragmentFactory
//        val fragment = fragmentFactory.instantiate(classLoader, name)
//        fragment.arguments = arguments ?: Bundle()
//        return fragment
//    }

    fun getCurrentIndex(@IdRes containerId: Int) = indexArrays.get(containerId)

    fun getFragments(@IdRes containerId: Int) = containerId.let {
        val fragments = fragmentArrays.get(it) ?: mutableListOf()
        fragmentArrays.put(containerId, fragments)
        fragments
    }

    /**
     * 获取[ViewPager2]容器名称
     * @param vp [ViewPager2]
     */
    private fun getContainerName(vp: ViewPager2) = vp.let {
        val name = ContextUtils.getIdName(vp.id)
        if (name.isEmpty()) "ViewPager2"
        else "ViewPager2[$name]"
    }

    /**
     * 生成Fragment适配器[GetMultiFragmentAdapter]
     * @param fragments [Fragment]列表
     */
    private fun generateFragmentAdapter(fragments: List<Fragment>): GetMultiFragmentAdapter {
        return if (lifecycleOwner is FragmentActivity) {
            GetMultiFragmentAdapter(lifecycleOwner, fragments)
        } else {
            GetMultiFragmentAdapter((lifecycleOwner as Fragment), fragments)
        }
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
            val adapter = if (lifecycleOwner is FragmentActivity) {
                GetMultiFragmentAdapter(lifecycleOwner, mutableListOf())
            } else {
                GetMultiFragmentAdapter((lifecycleOwner as Fragment), mutableListOf())
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
        val cacheCallback = callbackArrays.get(vp.hashCode())
        // 不存在回调说明未注册
        if (cacheCallback == null) {
            // 初始化回调函数
            val callback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 缓存索引
                    indexArrays.put(hashCode(), position)
                    // 打印日志
                    multiDelegate?.printLog(
                        LogLevel.D,
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
            callbackArrays.put(vp.hashCode(), callback)
        }
    }

    /**
     * 初始化[ViewPager2]容器
     * @param vp 容器
     */
    private fun initViewPager(vp: ViewPager2) {
        // 注册适配器
        registerFragmentAdapter(vp)
        // 注册回调监听
        registerOnPageChangeCallback(vp)
        // 关闭水波纹
        (vp.getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
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
        // 根据新的Fragment列表生成新的有效的索引
        val index = changeIndex(View.NO_ID, fragments)
        // 更新数据
        vp.adapter?.notifyDataSetChanged()
        // 设置当前项
        vp.setCurrentItem(index, vp.isUserInputEnabled)
        // 打印日志
        multiDelegate?.printLog(
            LogLevel.D,
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
                multiDelegate?.printLog(
                    LogLevel.W,
                    "showFragment",
                    "Index [$index] is invalid, there will do thing."
                )
                return
            }
        }
        // 设置当前项
        vp.setCurrentItem(index, vp.isUserInputEnabled)
        // 打印日志
        multiDelegate?.printLog(
            LogLevel.D,
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
        else multiDelegate?.printLog(
            LogLevel.W,
            "showFragment",
            "[${fragment.javaClass.simpleName}] can't be find in ${getContainerName(vp)}."
        )
    }

    fun addFragment(vp: ViewPager2, fragment: Fragment, index: Int) {
        // 对应Fragment列表
        val list = getFragments(vp)
        // 索引判断
        index.let {
            if (it !in (0..list.size)) {
                multiDelegate?.printLog(
                    LogLevel.W,
                    "addFragment",
                    "Index [$index] is invalid, there will do thing."
                )
                return
            }
        }
//        (vp.adapter as? GetMultiFragmentAdapter).let {
//            if (it == null) {
//
//            }
//        }

//        with(vp) {
//            adapter = this.adapter as? GetMultiFragmentAdapter ?: getFragmentAdapter(fragments).also { o -> it.adapter = o }
//            //val list = getFragments(this).also { it.add(index, fragment) }
//        }

//        viewPager?.let {
//            val adapter = it.adapter as? GetMultiFragmentAdapter
//                ?: getFragmentAdapter(fragments).also { o -> it.adapter = o }
//            adapter.fragments.clear()
//            adapter.fragments.addAll(fragments)
//            adapter.notifyDataSetChanged()
//            // 打印日志
//            printLog(newIndex)
//            // 显示新位置
//            showFragment(View.NO_ID, newIndex)
//        }
    }

    /**
     * 切换指定容器的Index
     * @param containerId 指定容器
     * @param fragments Fragment列表
     */
    private fun changeIndex(@IdRes containerId: Int, fragments: Array<Fragment>): Int {
        val index = containerId.let {
            // 获取指定容器的Index
            val index = getCurrentIndex(it)
            // 更新Index
            if (fragments.indices.contains(index)) index else 0
        }
        indexArrays.put(containerId, index)
        return index
    }


}