package cn.cqray.android.ui.multi

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.databinding.GetMultiLayoutBinding
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.Sizes
import cn.cqray.java.tool.SizeUnit
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.android.material.navigation.NavigationView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 多Fragment控制ViewModel
 * @author Cqray
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class GetMultiViewModel(
    lifecycleOwner: LifecycleOwner
) : GetViewModel(lifecycleOwner) {

    // 初始化
    init {
        if (lifecycleOwner !is GetMultiProvider) {
            throw IllegalArgumentException(
                String.format(
                    "%s must implement %s.",
                    javaClass.simpleName,
                    GetMultiProvider::class.java.simpleName
                )
            )
        }
    }

    /** ViewBinding **/
    val binding by lazy { GetMultiLayoutBinding.inflate(ContextUtils.layoutInflater) }

    /** 根控件 **/
    val multiView by lazy { binding.root }

    /** 内容布局 **/
    val multiContent : FrameLayout by lazy { binding.multiContent }

    /** 顶部[NavigationView] **/
    val multiTopNav: NavigationView by lazy { binding.multiTopNav }

    /** 底部[NavigationView] **/
    val multiBottomNav  : NavigationView by lazy { binding.multiBottomNav }

    /** [ViewPager2]组件 **/
    val multiPager by lazy {
        binding.multiPager.also {
            it.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    runCatching { multiTab.currentTab = position }
                }
            })
        }
    }

    /** TabLayout控件 **/
    val multiTab: CommonTabLayout by lazy {
        binding.multiTab.also {
            it.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    delegate.showFragment(multiPager, position)
                }

                override fun onTabReselect(position: Int) {}
            })
            // 赋值
            changeTabLocation(it)
        }
    }

    /** TabLayout是否在顶部 **/
    private val tabAtTop = AtomicBoolean(false)

    /** 多界面管理实例 **/
    private val delegate: GetMultiDelegate by lazy { (lifecycleOwner as GetMultiProvider).multiDelegate }

    /** Tab数据 **/
    private val tabData = ArrayList<CustomTabEntity>()

    /** Fragment列表 **/
    val fragments get() = delegate.getFragments(multiPager)

    /**
     * 改变TabLayout位置
     */
    private fun changeTabLocation(tabLayout: CommonTabLayout? = null) {
        // 从容器中移除
        (binding.multiTab.parent as ViewGroup).removeView(binding.multiTab)
        // 底部
        multiBottomNav.let {
            it.removeAllViews()
            if (!tabAtTop.get()) it.addView(tabLayout ?: this.multiTab)
        }
        // 顶部
        multiTopNav.let {
            it.removeAllViews()
            if (tabAtTop.get()) it.addView(tabLayout ?: this.multiTab)
        }
    }

    @Suppress("unchecked_cast")
    private fun changeTabData() {
        if (tabData.isEmpty()) {
            val field = CommonTabLayout::class.java.getDeclaredField("mTabEntitys")
            field.isAccessible = true
            val data = field.get(multiTab) as ArrayList<CustomTabEntity>
            data.clear()
            multiTab.notifyDataSetChanged()
        } else {
            multiTab.setTabData(tabData)
            // 改变缓存数量
            multiPager.offscreenPageLimit = tabData.size
        }
    }

    /**
     * 设置TabLayout在顶部
     * @param top 是否在顶部
     */
    fun setTabAtTop(top: Boolean) = tabAtTop.set(top).also { changeTabLocation() }

    /**
     * 设置TabLayout阴影高度
     * @param elevation 阴影高度
     */
    fun setTabElevation(elevation: Float) = setTabElevation(elevation, SizeUnit.DIP)

    /**
     * 设置TabLayout阴影高度
     * @param elevation 阴影高度
     * @param unit 单位
     */
    fun setTabElevation(elevation: Float, unit: SizeUnit) {
        val size = Sizes.applyDimension(elevation, unit)
        binding.multiTopNav.elevation = size
        binding.multiBottomNav.elevation = size
    }

    /**
     * 设置TabLayout高度
     * @param height 高度
     */
    fun setTabHeight(height: Float) = setTabHeight(height, SizeUnit.DIP)

    /**
     * 设置TabLayout高度
     * @param height 高度
     * @param unit 单位
     */
    fun setTabHeight(height: Float, unit: SizeUnit) {
        val size = Sizes.applyDimension(height, unit)
        multiTab.layoutParams.height = size.toInt()
    }

    /**
     * 是否允许用户拖拽
     * @param enable 启用
     */
    fun setFragmentDragEnable(enable: Boolean) = enable.let { multiPager.isUserInputEnabled = it }

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
        // 设置数据
        with(tabData) {
            clear()
            addAll(entries)
            changeTabData()
        }
        // 设置页面缓存量为Fragment数量，避免切换的时候被回收
        multiPager.offscreenPageLimit = if (items.isEmpty()) 5 else items.size
        // 加载Fragment
        delegate.loadMultiFragments(multiPager, Array(items.size) {
            val item = items[it]
            delegate.instantiateFragment(item.targetClass, item.arguments)
        })

    }

    /**
     * 显示指定位置Fragment
     * @param index 指定位置
     */
    fun showFragment(index: Int) {
        delegate.showFragment(multiPager, index)
        if (fragments.indices.contains(index)) {
            multiTab.currentTab = index
        }
    }

    /**
     * 显示指定Fragment
     * @param fragment 指定Fragment
     */
    fun showFragment(fragment: Fragment) {
        val index = fragments.indexOf(fragment)
        delegate.showFragment(multiPager, fragment)
        if (fragments.indices.contains(index)) {
            multiTab.currentTab = index
        }
    }

    /**
     * 添加Fragment
     * @param item 添加项
     * @param index 位置
     */
    fun addFragment(item: GetMultiItem, index: Int?) {
        // 生成新的Fragment
        val newIndex = (index ?: fragments.size).let {
            when {
                it < 0 -> 0
                it > fragments.size - 1 -> fragments.size
                else -> it
            }
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
        val fragment = delegate.instantiateFragment(item.targetClass, item.arguments)
        delegate.addFragment(multiPager, fragment, newIndex)
        // 设置multiPager2最大缓存数
        multiPager.offscreenPageLimit = fragments.size
    }

    /**
     * 移除指定Fragment
     * @param fragment 指定Fragment
     */
    fun removeFragment(fragment: Fragment) = removeFragment(fragments.indexOf(fragment))

    /**
     * 移除指定位置Fragment
     * @param index 指定位置
     */
    fun removeFragment(index: Int) {
        // 移除Fragment
        delegate.removeFragment(multiPager, index)
        // 有效index，则操作TabLayout
        if (index in tabData.indices) {
            runCatching {
                multiTab.currentTab = multiPager.currentItem
                tabData.removeAt(index)
                changeTabData()
            }
        }
    }

    /**
     * 移除所有Fragment
     */
    fun removeFragments() {
        tabData.clear()
        changeTabData()
        delegate.removeFragments(multiPager)
    }
}