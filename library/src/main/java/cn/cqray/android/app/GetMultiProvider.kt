package cn.cqray.android.app

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

/**
 * 多[Fragment]显示提供器
 * @author Cqray
 */
@Suppress("unused")
@JvmDefaultWithoutCompatibility
interface GetMultiProvider : GetProvider {

    val multiDelegate: GetMultiDelegate
        get() = GetDelegate.get(this, GetMultiProvider::class.java)

    /** [ViewPager2]下的当前索引 **/
    val currentIndex: Int
        get() = multiDelegate.currentIndex

    /** [ViewPager2]下的[Fragment]列表 **/
    val fragments: MutableList<Fragment>
        get() = multiDelegate.fragments

    /**
     * 获取指定容器的[Fragment]列表
     * @param containerId 指定容器ID
     */
    fun getFragments(@IdRes containerId: Int?) = multiDelegate.getFragments(containerId)

    /**
     * 获取指定容器的索引位置
     * @param containerId 指定容器ID
     */
    fun getCurrentIndex(@IdRes containerId: Int?) = multiDelegate.getCurrentIndex(containerId)

    /**
     * 生成Fragment列表
     * @param target 目标Fragment
     * @param arguments 参数
     */
    fun instantiateFragment(target: Class<out Fragment>, arguments: Bundle? = null) =
        multiDelegate.instantiateFragment(target, arguments)

    /**
     * 在指定容器中，加载多个Fragment
     * @param containerId 容器Id
     * @param fragments Fragment列表
     */
    fun loadMultiFragments(@IdRes containerId: Int?, fragments: Array<Fragment>) =
        multiDelegate.loadMultiFragments(containerId, fragments)

    /**
     * 加载多个Fragment
     * @param vp [ViewPager2]容器
     * @param fragments Fragment列表
     */
    fun loadMultiFragments(vp: ViewPager2, fragments: Array<Fragment>) = multiDelegate.loadMultiFragments(vp, fragments)

    /**
     * 显示指定容器指定位置的[Fragment]
     * @param containerId   指定容器ID
     * @param index         指定位置
     */
    fun showFragment(@IdRes containerId: Int?, index: Int?) = multiDelegate.showFragment(containerId, index)

    /**
     * 添加Fragment界面
     * @param containerId 容器ID
     * @param fragment Fragment
     */
    fun showFragment(@IdRes containerId: Int?, fragment: Fragment) = multiDelegate.showFragment(containerId, fragment)

    /**
     * 添加Fragment界面
     * @param containerId 容器ID
     * @param fragment Fragment
     */
    fun addFragment(
        @IdRes containerId: Int?, fragment: Fragment, index: Int?
    ) = multiDelegate.addFragment(containerId, fragment, index)

    /**
     * 移除指定的Fragment
     * @param index 位置
     */
    fun removeFragment(@IdRes containerId: Int?, index: Int?) = multiDelegate.removeFragment(containerId, index)

    /**
     * 移除指定的Fragment
     * @param fragment Fragment界面
     */
    fun removeFragment(@IdRes containerId: Int?, fragment: Fragment) =
        multiDelegate.removeFragment(containerId, fragment)

    /**
     * 移除指定容器下所有的Fragment
     * @param containerId 指定容器ID
     */
    fun removeFragments(@IdRes containerId: Int? = null) = multiDelegate.removeFragments(containerId)

    /**
     * 移除所有[Fragment]
     */
    fun removeAllFragments() = multiDelegate.removeAllFragments()
}