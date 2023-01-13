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
import cn.cqray.android.log.GetLog
import cn.cqray.android.log.LogLevel
import cn.cqray.android.util.Sizes
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener

/**
 * 多Fragment控制ViewModel
 * @author Cqray
 */
internal class GetMultiFragmentViewModel(lifecycleOwner: LifecycleOwner) :
    GetViewModel(lifecycleOwner) {

    /** TabLayout是否在顶部 **/
    var tabAtTop: Boolean = false
        private set

    /** ViewBinding实例 **/
    private var binding: GetLayoutMultiTabBinding

    /** 多界面管理实例 **/
    private val delegate: GetMultiDelegate?

    /** Tab数据 **/
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
        // 初始化[GetMultiActivity]及[GetMultiFragment]的控件
        initGetMultiView()
    }

    /** 获取根控件 **/
    val rootView: View get() = binding.root

    /** [ViewPager2]组件 **/
    @Suppress
    val viewPager: ViewPager2
        get() {
            val vp = when (lifecycleOwner) {
                is GetMultiActivity -> lifecycleOwner.mViewPager
                is GetMultiFragment -> lifecycleOwner.mViewPager
                else -> null
            }
            return vp ?: binding.getNavContent
        }

    /** TabLayout控件 **/
    @Suppress
    val tabLayout: CommonTabLayout
        get() {
            val vp = when (lifecycleOwner) {
                is GetMultiActivity -> lifecycleOwner.mTabLayout
                is GetMultiFragment -> lifecycleOwner.mTabLayout
                else -> null
            }
            return vp ?: binding.getNavTab
        }

    /**
     * 初始化ViewPager2
     */
    private fun initViewPager() {
        binding.getNavContent.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {

                    tabLayout.currentTab = position
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
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
        when (lifecycleOwner) {
            // 给GetMultiActivity控件赋值
            is GetMultiActivity -> {
                lifecycleOwner.mViewPager = viewPager
                lifecycleOwner.mTabLayout = tabLayout
            }
            // 给GetMultiFragment控件赋值
            is GetMultiFragment -> {
                lifecycleOwner.mViewPager = viewPager
                lifecycleOwner.mTabLayout = tabLayout
            }
        }
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
            if (!(0 until it.fragments.size).contains(newIndex)) {
                it.printLog(LogLevel.W, "showFragment","[$index] is invalid, there will do nothing.")
                return
            }
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
            if (!(0 until it.fragments.size).contains(newIndex)) {
                it.printLog(LogLevel.W, "removeFragment","[$index] is invalid, there will remove nothing.")
                return
            }
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
        changeTabData()
        delegate?.removeFragments(View.NO_ID)
    }
}