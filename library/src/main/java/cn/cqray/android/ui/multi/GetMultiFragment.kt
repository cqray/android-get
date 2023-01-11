package cn.cqray.android.ui.multi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetFragment
import cn.cqray.android.app.GetMultiItem
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.lifecycle.GetViewModelProvider
import com.flyco.tablayout.CommonTabLayout

/**
 * 多Fragment界面
 * @author Cqray
 */
open class GetMultiFragment : GetFragment(), GetMultiProvider {

    /** [GetMultiFragmentViewModel]实例 **/
    private lateinit var viewModel: GetMultiFragmentViewModel

    /** [ViewPager2]实例，同[viewPager] **/
    @NonNull
    @JvmField
    @SuppressLint("KotlinNullnessAnnotation")
    var mViewPager: ViewPager2? = null

    /** [CommonTabLayout]实例，同[tabLayout] **/
    @NonNull
    @JvmField
    @SuppressLint("KotlinNullnessAnnotation")
    var mTabLayout: CommonTabLayout? = null

    /** [ViewPager2]实例，同[mViewPager] **/
    @Suppress
    val viewPager: ViewPager2
        get() = viewModel.viewPager

    /** [CommonTabLayout]实例，同[mTabLayout] **/
    @Suppress
    val tabLayout: CommonTabLayout
        get() = viewModel.tabLayout

    /** TabLayout是否在头部 **/
    @Suppress
    val tabAtTop
        get() = viewModel.tabAtTop

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        viewModel = GetViewModelProvider(this).get(GetMultiFragmentViewModel::class.java)
        setNativeContentView(viewModel.rootView)
    }

    fun setTabAtTop(tabAtTop: Boolean?) = viewModel.setTabAtTop(tabAtTop)

    fun setTabElevation(elevation: Float?) = viewModel.setTabElevation(elevation)

    fun setTabHeight(height: Float?) = viewModel.setTabHeight(height)

    fun setFragmentDragEnable(enable: Boolean?) = viewModel.setFragmentDragEnable(enable)

    fun loadMultiFragments(vararg items: GetMultiItem) = viewModel.loadMultiFragments(*items)

    override fun showFragment(fragment: Fragment) = viewModel.showFragment(fragment)

    override fun showFragment(index: Int?) = viewModel.showFragment(index)

    override fun removeFragment(index: Int?) = viewModel.removeFragment(index)

    override fun removeFragment(fragment: Fragment) = viewModel.removeFragment(fragment)

    override fun removeFragments() = viewModel.removeFragments()

}