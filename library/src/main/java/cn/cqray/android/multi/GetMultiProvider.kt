package cn.cqray.android.multi

import android.os.Bundle
import androidx.annotation.Px
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ScrollState
import cn.cqray.android.app.GetDelegate
import cn.cqray.android.app.GetProvider

/**
 * 多[Fragment]显示提供器
 * @author Cqray
 */
@Suppress("unused")
@JvmDefaultWithoutCompatibility
interface GetMultiProvider : GetProvider {

    val multiDelegate: GetMultiDelegate
        get() = GetDelegate.get(this, GetMultiProvider::class.java)

    /**
     * 获取指定容器的[Fragment]列表
     * @param vp 指定容器
     */
    fun getFragments(vp: ViewPager2) = multiDelegate.getFragments(vp)

    /**
     * 获取指定容器的[Fragment]位置索引
     * @param vp 指定容器
     */
    fun getCurrentIndex(vp: ViewPager2) = vp.currentItem

    /**
     * 生成Fragment列表
     * @param target 目标Fragment
     * @param arguments 参数
     */
    fun instantiateFragment(target: Class<out Fragment>, arguments: Bundle? = null) =
        multiDelegate.instantiateFragment(target, arguments)

    /**
     * 加载多个Fragment
     * @param vp [ViewPager2]容器
     * @param fragments Fragment列表
     */
    fun loadMultiFragments(vp: ViewPager2, fragments: Array<Fragment>) = multiDelegate.loadMultiFragments(vp, fragments)

    /**
     * 显示指定容器指定位置的[Fragment]
     * @param vp 指定容器
     * @param index 指定位置
     */
    fun showFragment(vp: ViewPager2, index: Int) = multiDelegate.showFragment(vp, index)

    /**
     * 添加Fragment界面
     * @param vp 指定容器
     * @param fragment Fragment
     */
    fun showFragment(vp: ViewPager2, fragment: Fragment) = multiDelegate.showFragment(vp, fragment)

    /**
     * 添加Fragment界面
     * @param vp 指定容器
     * @param fragment Fragment
     */
    fun addFragment(vp: ViewPager2, fragment: Fragment) = multiDelegate.addFragment(vp, fragment, null)

    /**
     * 添加Fragment界面
     * @param vp 指定容器
     * @param fragment Fragment
     * @param index 位置
     */
    fun addFragment(vp: ViewPager2, fragment: Fragment, index: Int?) = multiDelegate.addFragment(vp, fragment, index)

    /**
     * 移除指定容器的Fragment
     * @param vp 指定容器
     * @param index 位置
     */
    fun removeFragment(vp: ViewPager2, index: Int) = multiDelegate.removeFragment(vp, index)

    /**
     * 移除指定的Fragment
     * @param fragment Fragment界面
     */
    fun removeFragment(vp: ViewPager2, fragment: Fragment) = multiDelegate.removeFragment(vp, fragment)

    /**
     * 移除指定容器下所有的Fragment
     * @param vp 指定容器
     */
    fun removeFragments(vp: ViewPager2) = multiDelegate.removeFragments(vp)

    /**
     * Fragment界面发生变化
     * @param vp 所处容器
     * @param position 改变后的位置
     */
    fun onFragmentPageSelected(vp: ViewPager2, position: Int) {}
}