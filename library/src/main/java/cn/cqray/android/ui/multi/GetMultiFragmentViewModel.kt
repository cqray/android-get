package cn.cqray.android.ui.multi

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetMultiItem
import cn.cqray.android.app.delegate.GetMultiDelegate
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.databinding.GetLayoutMultiTabBinding
import cn.cqray.android.lifecycle.GetViewModel
import cn.cqray.android.util.Sizes
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener

/**
 * 多Fragment控制ViewModel
 * @author Cqray
 */
internal class GetMultiFragmentViewModel(lifecycleOwner: LifecycleOwner) : GetViewModel(lifecycleOwner) {

    /** TabLayout是否在顶部 **/
    var tabAtTop: Boolean = false
        private set

    private var binding: GetLayoutMultiTabBinding

    private val delegate: GetMultiDelegate?

    private val tabData = ArrayList<CustomTabEntity>()

    // 初始化
    init {
        val layoutInflater = if (lifecycleOwner is Fragment) {
            lifecycleOwner.layoutInflater
        } else (lifecycleOwner as FragmentActivity).layoutInflater
        // 获取多Fragment委托
        delegate = (lifecycleOwner as? GetMultiProvider)?.multiDelegate
        // 初始化Binding
        binding = GetLayoutMultiTabBinding.inflate(layoutInflater)
        // 初始化ViewPager2
        initViewPager()
        // 初始化TabLayout
        initTabLayout()
    }

    /** 获取根控件 **/
    val rootView: View get() = binding.root

    /** [ViewPager2]组件 **/
    val viewPager: ViewPager2 get() = binding.getNavContent

    /** TabLayout控件 **/
    val tabLayout: CommonTabLayout get() = binding.getNavTab

    /**
     * 初始化ViewPager2
     */
    private fun initViewPager() {
        binding.getNavContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.currentTab = position
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
     * 改变TabLayout位置
     */
    private fun changeTabLocation() {
        binding.getBottomNav.removeAllViews()
        binding.getTopNav.removeAllViews()
        if (tabAtTop) binding.getTopNav.addView(tabLayout)
        else binding.getBottomNav.addView(tabLayout)
    }

    /**
     * 设置TabLayout在顶部
     * @param top 是否在顶部
     */
    fun setTabAtTop(top: Boolean?) = top?.let {
        tabAtTop = it
        changeTabLocation()
    }

    /**
     * 设置TabLayout阴影高度
     * @param elevation 阴影高度
     */
    fun setTabElevation(elevation: Float?) {
        elevation?.let {
            val size = Sizes.dp2px(elevation)
            binding.getTopNav.elevation = size.toFloat()
            binding.getBottomNav.elevation = size.toFloat()
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
        val entries = ArrayList<CustomTabEntity>();
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
            tabLayout.setTabData(this)
        }
        // 设置ViewPager2最大缓存数
        viewPager.offscreenPageLimit = items.size
        // 加载Fragment
        delegate?.loadMultiFragments(viewPager, arrayOf(*items))
    }

    /**
     * 显示指定位置Fragment
     * @param index 指定位置
     */
    fun showFragment(index: Int?) {
        delegate?.let {
            val newIndex = index ?: 0
            if (!(0 until it.fragments.size).contains(newIndex)) return
            it.showFragment(View.NO_ID, newIndex)
            tabLayout.currentTab = newIndex
        }
    }

    /**
     * 显示指定Fragment
     * @param fragment 指定Fragment
     */
    fun showFragment(fragment: Fragment) {
        delegate?.let {
            val index = it.fragments.indexOf(fragment)
            showFragment(index)
        }
    }

    /**
     * 移除指定位置Fragment
     * @param index 指定位置
     */
    fun removeFragment(index: Int?) {
        delegate?.let {
            val newIndex = index ?: 0
            if (!(0 until it.fragments.size).contains(newIndex)) return
            it.removeFragment(View.NO_ID, newIndex)
            tabData.removeAt(newIndex)
            tabLayout.setTabData(tabData)
            tabLayout.currentTab = it.currentIndex
        }
    }

    /**
     * 移除指定Fragment
     * @param fragment 指定Fragment
     */
    fun removeFragment(fragment: Fragment) {
        delegate?.let {
            val index = it.fragments.indexOf(fragment)
            removeFragment(index)
        }
    }

    /**
     * 移除所有Fragment
     */
    fun removeFragments() {
        tabData.clear()
        tabLayout.setTabData(tabData)
        delegate?.removeFragments(View.NO_ID)
    }
}