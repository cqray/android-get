package cn.cqray.android.app.provider

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.delegate.GetDelegate
import cn.cqray.android.app.delegate.GetMultiDelegate

/**
 * 多[Fragment]显示提供器
 * @author Cqray
 */
@Suppress("unused")
@JvmDefaultWithoutCompatibility
interface GetMultiProvider: GetProvider {

    val multiDelegate: GetMultiDelegate
        get() = GetDelegate.get(this, GetMultiProvider::class.java)

    val currentIndex: Int
        get() = multiDelegate.currentIndex

    val fragments: MutableList<Fragment>
        get() = multiDelegate.fragments

    fun showFragment(index: Int?) = multiDelegate.showFragment(View.NO_ID, index)

    fun showFragment(fragment: Fragment) = multiDelegate.showFragment(View.NO_ID,fragment)

//    fun addFragment(cls: Class<out Fragment>) = multiDelegate.addFragment(View.NO_ID, cls)

    fun addFragment(fragment: Fragment) = multiDelegate.addFragment(View.NO_ID, fragment)

    fun removeFragment(index: Int?) = multiDelegate.removeFragment(View.NO_ID, index)

    fun removeFragment(fragment: Fragment) = multiDelegate.removeFragment(View.NO_ID, fragment)

    fun removeFragments() = multiDelegate.removeFragments(View.NO_ID)

}