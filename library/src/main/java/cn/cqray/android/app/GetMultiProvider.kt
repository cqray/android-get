package cn.cqray.android.app

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

/**
 * 多[Fragment]显示提供器
 * @author Cqray
 */
@Suppress("unused")
@JvmDefaultWithoutCompatibility
interface GetMultiProvider {

    val multiDelegate: GetMultiDelegate
        get() = GetMultiDelegate.get(this)

    val currentIndex: Int
        get() = multiDelegate.currentIndex

    val fragments: MutableList<Fragment>
        get() = multiDelegate.fragments

//    fun loadMultiFragments(@IdRes containerId: Int, vararg fragments: Fragment) {
//        val list = listOf(*fragments)
//        multiDelegate.loadMultiFragments(containerId, list)
//    }
//
//    fun loadMultiFragments(@IdRes containerId: Int, vararg fragments: Fragment) {
//        val list = listOf(*fragments)
//        multiDelegate.loadMultiFragments(containerId, list)
//    }
//
//    fun loadMultiFragments(vp: ViewPager2, vararg fragments: Fragment) {
//        val list = listOf(*fragments)
//        multiDelegate.loadMultiFragments(vp, list)
//    }



//    fun loadMultiFragments(vp: ViewPager2, vararg intents: GetIntent?) {
//        mMultiDelegate.loadMultiFragments(vp, intents)
//    }
//
//    fun loadMultiFragemts(vp: ViewPager2, vararg classes: Class<out SupportProvider?>) {
//        val intents = arrayOfNulls<GetIntent>(classes.size)
//        for (i in 0 until classes.size) {
//            intents[i] = GetIntent(classes[i] as Class<out SupportProvider?>)
//        }
//        mMultiDelegate.loadMultiFragments(vp, intents)
//    }

    fun showFragment(index: Int?) = multiDelegate.showFragment(index)

    fun showFragment(fragment: Fragment) = multiDelegate.showFragment(fragment)

    fun addFragment(cls: Class<out Fragment>) = multiDelegate.addFragment(cls)

    fun addFragment(fragment: Fragment) = multiDelegate.addFragment(fragment)

    fun removeFragment(index: Int?) = multiDelegate.removeFragment(index)

    fun removeFragment(fragment: Fragment) = multiDelegate.removeFragment(fragment)

    fun resetFragments() = multiDelegate.reset()
}