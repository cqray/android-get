package cn.cqray.android.ui.multi

import android.os.Bundle
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetFragment
import com.flyco.tablayout.CommonTabLayout

/**
 * 多Fragment界面
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
open class GetMultiFragment : GetFragment(), GetMultiProvider {

    /** [GetMultiViewModel]实例 **/
    private val viewModel: GetMultiViewModel by lazy {
        val viewModel = ViewModelProvider(this).get(GetMultiViewModel::class.java)
        viewModel.init(multiDelegate)
        viewModel
    }

    /** 根布局 **/
    val multiView get() = viewModel.multiView

    /** [ViewPager2]实例 **/
    val multiPager get() = viewModel.multiPager

    /** [CommonTabLayout]实例 **/
    val multiTab get() = viewModel.multiTab

    /** 内容布局，[ViewPager2]的父容器 **/
    val multiContent get() = viewModel.multiContent

    /** 顶部Nav容器 **/
    val multiTopNav get() = viewModel.multiTopNav

    /** 底部Nav容器 **/
    val multiBottomNav get() = viewModel.multiBottomNav

    /** 当前位置索引 **/
    val currentIndex get() = multiPager.currentItem

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(viewModel.multiView)
    }

    fun setTabAtTop(tabAtTop: Boolean) = viewModel.setTabAtTop(tabAtTop)

    @JvmOverloads
    fun setTabElevation(elevation: Number, unit: Int = COMPLEX_UNIT_DIP) = viewModel.setTabElevation(elevation, unit)

    @JvmOverloads
    fun setTabHeight(height: Number, unit: Int = COMPLEX_UNIT_DIP) = viewModel.setTabHeight(height, unit)

    fun setFragmentDragEnable(enable: Boolean) = viewModel.setFragmentDragEnable(enable)

    fun loadMultiFragments(vararg items: GetMultiItem) = viewModel.loadMultiFragments(*items)

    fun showFragment(fragment: Fragment) = viewModel.showFragment(fragment)

    fun showFragment(index: Int) = viewModel.showFragment(index)

    fun addFragment(item: GetMultiItem, index: Int?) = viewModel.addFragment(item, index)

    fun removeFragment(index: Int) = viewModel.removeFragment(index)

    fun removeFragment(fragment: Fragment) = viewModel.removeFragment(fragment)

    fun removeFragments() = viewModel.removeFragments()
}