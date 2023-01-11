package cn.cqray.android.ui.multi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetActivity
import cn.cqray.android.app.GetMultiItem
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.lifecycle.GetViewModelProvider
import com.flyco.tablayout.CommonTabLayout

/**
 * 底部多Fragment界面
 * @author Cqray
 */
open class GetMultiActivity : GetActivity(), GetMultiProvider {

    private lateinit var viewModel: GetMultiFragmentViewModel

    @Suppress
    val viewPager: ViewPager2
        get() = viewModel.viewPager

//    @Suppress
//    val tabLayout: CommonTabLayout
//        get() = viewModel.tabLayout

    @NonNull
    @JvmField
    @SuppressLint("KotlinNullnessAnnotation")
    var tabLayout: CommonTabLayout? = null


//        protected set

//    @JvmField
//    val tabLayout: CommonTabLayout by lazy { viewModel.tabLayout }


    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        viewModel = GetViewModelProvider(this).get(GetMultiFragmentViewModel::class.java)
        setNativeContentView(viewModel.rootView)

        tabLayout = viewModel.tabLayout

    }

    val tabAtTop get() = viewModel.tabAtTop

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