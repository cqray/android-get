package cn.cqray.android.ui.multi

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.R
import cn.cqray.android.app.GetActivity
import cn.cqray.android.app.GetMultiProvider
import cn.cqray.android.lifecycle.GetViewModelProvider
import com.flyco.tablayout.CommonTabLayout
import com.google.android.material.navigation.NavigationView

/**
 * 底部多Fragment界面
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class GetMultiActivity : GetActivity(), GetMultiProvider {

    /** [GetMultiViewModel]实例 **/
    private lateinit var viewModel: GetMultiViewModel

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

    val viewPager: ViewPager2 get() = viewModel.viewPager

    /** [CommonTabLayout]实例，同[mTabLayout] **/

    val tabLayout: CommonTabLayout get() = viewModel.tabLayout

    /** TabLayout是否在头部 **/
    val tabAtTop get() = viewModel.tabAtTop

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        viewModel = GetViewModelProvider(this).get(GetMultiViewModel::class.java)
        setNativeContentView(viewModel.rootView)
    }

    fun setTabAtTop(tabAtTop: Boolean?) = viewModel.setTabAtTop(tabAtTop)

    fun setTabElevation(elevation: Float?) = viewModel.setTabElevation(elevation)

    fun setTabHeight(height: Float?) = viewModel.setTabHeight(height)

    fun setFragmentDragEnable(enable: Boolean?) = viewModel.setFragmentDragEnable(enable)

    fun loadMultiFragments(vararg items: GetMultiItem) = viewModel.loadMultiFragments(*items)

    fun showFragment(fragment: Fragment) = viewModel.showFragment(fragment)

    fun showFragment(index: Int?) = viewModel.showFragment(index)

    fun addFragment(item: GetMultiItem, index: Int?) = viewModel.addFragment(item, index)

    fun removeFragment(index: Int?) = viewModel.removeFragment(index)

    fun removeFragment(fragment: Fragment) = viewModel.removeFragment(fragment)

    fun removeFragments() = viewModel.removeFragments()

}