package cn.cqray.android.multi

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.R
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.ReflectUtil
import cn.cqray.android.util.Sizes
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.android.material.navigation.NavigationView

/**
 * 多Fragment控制ViewModel
 * @author Cqray
 */
@Suppress("StaticFieldLeak", "MemberVisibilityCanBePrivate")
internal class GetMultiViewModel(lifecycleOwner: LifecycleOwner) : GetViewModel(lifecycleOwner) {

    /** 所有控件 **/
    private val views = MutableList<View?>(5) { null }

    /** 位置信息，TabLayout是否在顶部 **/
    private val location = arrayOf(false)

    /** 多界面管理实例 **/
    private val delegate: GetMultiDelegate?

    /** Tab数据 **/
    private val tabData = ArrayList<CustomTabEntity>()

    // 初始化
    init {
        // 初始化控件
        views[0] = ContextUtils.inflate(R.layout.get_layout_multi_tab)
        views[0]?.let {
            views[1] = it.findViewById(R.id.get_top_nav)
            views[2] = it.findViewById(R.id.get_bottom_nav)
            views[3] = it.findViewById(R.id.get_nav_content)
            views[4] = it.findViewById(R.id.get_nav_tab)
        }
        // 获取多Fragment委托
        delegate = (lifecycleOwner as? GetMultiProvider)?.multiDelegate
        // 初始化ViewPager2
        initViewPager()
        // 初始化TabLayout
        initTabLayout()
        // 初始化[GetMultiActivity]及[GetMultiFragment]的控件
        initGetMultiView()
    }

    /** TabLayout是否在顶部 **/
    val tabAtTop: Boolean get() = location[0]

    /** 根布局 **/
    val rootView: View get() = views[0]!!

    /** [ViewPager2]组件 **/
    val viewPager: ViewPager2 get() = views[3] as ViewPager2

    /** TabLayout控件 **/
    val tabLayout: CommonTabLayout get() = views[4] as CommonTabLayout

    override fun onCleared() {
        super.onCleared()
        views.clear()
    }

    /**
     * 初始化ViewPager2
     */
    private fun initViewPager() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    tabLayout.currentTab = position
                } catch (ignore: Exception) {
                }
            }
        })
    }

    /**
     * 初始化TabLayout
     */
    private fun initTabLayout() {
        val listener = object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                delegate?.showFragment(View.NO_ID, position)
            }

            override fun onTabReselect(position: Int) {}
        }
        tabLayout.setOnTabSelectListener(listener)
        changeTabLocation()
    }

    /**
     * 初始化[GetMultiActivity]及[GetMultiFragment]的控件
     */
    private fun initGetMultiView() {
        if (lifecycleOwner is GetMultiActivity
            || lifecycleOwner is GetMultiFragment
        ) {
            ReflectUtil.setField(lifecycleOwner, "viewPager", viewPager)
            ReflectUtil.setField(lifecycleOwner, "tabLayout", tabLayout)
        }
    }

    /**
     * 改变TabLayout位置
     */
    private fun changeTabLocation() {
        // 底部
        (views[2] as? ViewGroup)?.let {
            it.removeAllViews()
            if (!tabAtTop) it.addView(tabLayout)
        }
        // 顶部
        (views[1] as? ViewGroup)?.let {
            it.removeAllViews() 
            if (tabAtTop) it.addView(tabLayout)
        }
    }

    @Suppress("unchecked_cast")
    private fun changeTabData() {
        if (tabData.isEmpty()) {
            val field = CommonTabLayout::class.java.getDeclaredField("mTabEntitys")
            field.isAccessible = true
            val data = field.get(tabLayout) as ArrayList<CustomTabEntity>
            data.clear()
            tabLayout.notifyDataSetChanged()
        } else {
            tabLayout.setTabData(tabData)
            // 改变缓存数量
            viewPager.offscreenPageLimit = tabData.size
        }
    }

    /**
     * 设置TabLayout在顶部
     * @param top 是否在顶部
     */
    fun setTabAtTop(top: Boolean?) = top?.let {
        location[0] = it
        changeTabLocation()
    }

    /**
     * 设置TabLayout阴影高度
     * @param elevation 阴影高度
     */
    fun setTabElevation(elevation: Float?) {
        elevation?.let {
            val size = Sizes.dp2px(elevation)
            (views[1] as? NavigationView)?.elevation = size.toFloat()
            (views[2] as? NavigationView)?.elevation = size.toFloat()
        }
    }

    /**
     * 设置TabLayout高度
     * @param height 高度
     */
    fun setTabHeight(height: Float?) {
        height?.let {
            val size = Sizes.dp2px(height)
            tabLayout.layoutParams.height = size
        }
    }

    fun setFragmentDragEnable(enable: Boolean?) = enable?.let {
        // 是否允许用户输入
        viewPager.isUserInputEnabled = it
    }

    /**
     * 加载多个Fragment
     */
    fun loadMultiFragments(vararg items: GetMultiItem) {
        // 初始化TabLayout
        val entries = ArrayList<CustomTabEntity>()
        items.forEach {
            val entry = object : CustomTabEntity {
                override fun getTabTitle() = it.name ?: ""

                override fun getTabSelectedIcon() = it.selectIcon ?: 0

                override fun getTabUnselectedIcon() = it.unselectIcon ?: 0
            }
            entries.add(entry)
        }
        with(tabData) {
            clear()
            addAll(entries)
            changeTabData()
        }
        // 设置ViewPager2最大缓存数
        viewPager.offscreenPageLimit = if (items.isEmpty()) 5 else items.size
        // 加载Fragment
        delegate?.loadMultiFragments(viewPager, Array(items.size) {
            val item = items[it]
            delegate.instantiateFragment(item.targetClass, item.arguments)
        })
    }

    /**
     * 显示指定位置Fragment
     * @param index 指定位置
     */
    fun showFragment(index: Int?) {
        delegate?.let {
            val newIndex = index ?: -1
            it.showFragment(View.NO_ID, newIndex)
            if ((0 until it.fragments.size).contains(newIndex)) {
                tabLayout.currentTab = newIndex
            }
        }
    }

    /**
     * 显示指定Fragment
     * @param fragment 指定Fragment
     */
    fun showFragment(fragment: Fragment) {
        delegate?.let {
            val index = it.fragments.indexOf(fragment)
            it.showFragment(View.NO_ID, fragment)
            if ((0 until it.fragments.size).contains(index)) {
                tabLayout.currentTab = index
            }
        }
    }

    /**
     * 添加Fragment
     * @param item 添加项
     * @param index 位置
     */
    fun addFragment(item: GetMultiItem, index: Int?) {
        delegate?.let {
            // 生成新的Fragment
            val newIndex = (index ?: it.fragments.size).let { index ->
                if (index < 0) 0
                else if (index > it.fragments.size - 1) it.fragments.size
                else index
            }
            // 更新Tab的数据
            val entry = object : CustomTabEntity {
                override fun getTabTitle() = item.name ?: ""

                override fun getTabSelectedIcon() = item.selectIcon ?: 0

                override fun getTabUnselectedIcon() = item.unselectIcon ?: 0
            }
            tabData.add(newIndex, entry)
            changeTabData()
            // 添加新的Fragment
            val fragment = it.instantiateFragment(item.targetClass, item.arguments)
            it.addFragment(View.NO_ID, fragment, newIndex)
        }
    }

    /**
     * 移除指定位置Fragment
     * @param index 指定位置
     */
    fun removeFragment(index: Int?) {
        delegate?.let {
            val newIndex = index ?: -1
            val fragments = it.fragments
            // 移除Fragment
            it.removeFragment(View.NO_ID, newIndex)
            // 有效index，则操作TabLayout
            if ((0 until fragments.size).contains(newIndex)) {
                tabData.removeAt(newIndex)
                tabLayout.setTabData(tabData)
                tabLayout.currentTab = it.currentIndex
            }
        }
    }

    /**
     * 移除指定Fragment
     * @param fragment 指定Fragment
     */
    fun removeFragment(fragment: Fragment) {
        delegate?.let {
            val fragments = it.fragments
            val index = fragments.indexOf(fragment)
            // 移除Fragment
            it.removeFragment(View.NO_ID, fragment)
            // 合法index，则操作TabLayout
            if ((0 until fragments.size).contains(index)) {
                tabData.removeAt(index)
                tabLayout.setTabData(tabData)
                tabLayout.currentTab = it.currentIndex
            }
        }
    }

    /**
     * 移除所有Fragment
     */
    fun removeFragments() {
        tabData.clear()
        changeTabData()
        delegate?.removeFragments(View.NO_ID)
    }
}