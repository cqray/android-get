package cn.cqray.android.multi

import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetFragment
import cn.cqray.android.lifecycle.GetViewModelProvider
import com.flyco.tablayout.CommonTabLayout

/**
 * 多Fragment界面
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class GetMultiFragment : GetFragment(), GetMultiProvider {

    /** [GetMultiViewModel]实例 **/
    private val viewModel: GetMultiViewModel by lazy {
        GetViewModelProvider(this).get(GetMultiViewModel::class.java)
    }

    /** [ViewPager2]实例 **/
    @Keep
    @NonNull
    @JvmField
    @Suppress("KotlinNullnessAnnotation")
    val viewPager: ViewPager2? = null

    /** [CommonTabLayout]实例 **/
    @Keep
    @NonNull
    @JvmField
    @Suppress("KotlinNullnessAnnotation")
    val tabLayout: CommonTabLayout? = null

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(viewModel.rootView)
    }

    /** TabLayout是否在头部 **/
    fun isTabAtTop() = viewModel.tabAtTop

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