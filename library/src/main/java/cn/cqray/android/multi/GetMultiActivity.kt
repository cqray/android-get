package cn.cqray.android.multi

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetActivity
import cn.cqray.android.util.SizeUnit
import com.flyco.tablayout.CommonTabLayout

/**
 * 底部多Fragment界面
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class GetMultiActivity : GetActivity(), GetMultiProvider {

    /** [GetMultiViewModel]实例 **/
    private val viewModel: GetMultiViewModel by lazy { getViewModel(GetMultiViewModel::class.java) }

    /** [ViewPager2]实例 **/
    val viewPager get() = viewModel.viewPager

    /** [CommonTabLayout]实例 **/
    val tabLayout get() = viewModel.tabLayout

    /** 当前位置索引 **/
    val currentIndex get() = viewPager.currentItem

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(viewModel.rootView)
    }

    fun setTabAtTop(tabAtTop: Boolean) = viewModel.setTabAtTop(tabAtTop)

    fun setTabElevation(elevation: Float) = viewModel.setTabElevation(elevation)

    fun setTabElevation(elevation: Float, unit: SizeUnit) = viewModel.setTabElevation(elevation, unit)

    fun setTabHeight(height: Float) = viewModel.setTabHeight(height)

    fun setTabHeight(height: Float, unit: SizeUnit) = viewModel.setTabHeight(height, unit)

    fun setFragmentDragEnable(enable: Boolean) = viewModel.setFragmentDragEnable(enable)

    fun loadMultiFragments(vararg items: GetMultiItem) = viewModel.loadMultiFragments(*items)

    fun showFragment(fragment: Fragment) = viewModel.showFragment(fragment)

    fun showFragment(index: Int) = viewModel.showFragment(index)

    fun addFragment(item: GetMultiItem, index: Int?) = viewModel.addFragment(item, index)

    fun removeFragment(index: Int) = viewModel.removeFragment(index)

    fun removeFragment(fragment: Fragment) = viewModel.removeFragment(fragment)

    fun removeFragments() = viewModel.removeFragments()
}