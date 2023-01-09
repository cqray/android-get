package cn.cqray.android.ui.multi

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.cqray.android.R
import cn.cqray.android.app.GetActivity
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.provider.GetMultiProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 底部多Fragment界面
 * @author Cqray
 */
@Suppress("unused")
open class MultiNavActivity : GetActivity(), GetMultiProvider {

    /** [ViewPager2]容器 **/
    @Suppress("unused")
    lateinit var viewPager: ViewPager2

    /** 底部导航栏[BottomNavigationView] **/
    @Suppress("unused")
    lateinit var navView: BottomNavigationView

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(R.layout.get_layout_multi_bottom)
        // ViewPager2容器
        viewPager = findViewById(R.id.get_nav_content)
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navView.selectedItemId = position
            }
        })
        // 顶部导航栏
        navView = findViewById(R.id.get_nav_view)
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            item.isChecked = true
            multiDelegate.showFragment(View.NO_ID, item.order)
            false
        }
    }

    fun loadMultiFragments(vararg items: MultiItem) {
        removeFragments()
        val menu = navView.menu
        menu.clear()
        val intents = Array<GetIntent>(items.size) { items[it].intent }
        for (i in items.indices) {
            intents[i] = items[i].intent
            val ti = items[i]
            menu.add(0, i, i, ti.name).setIcon(ti.icon)
        }
        viewPager.offscreenPageLimit = items.size
        multiDelegate.loadMultiFragments(viewPager, intents)
    }

    /**
     * 设置[Fragment]是否可以左右滑动
     * @param enable 是否可以左右滑动
     */
    fun setFragmentDragEnable(enable: Boolean?) {
        // 设置ViewPager是否可以左右滑动
        viewPager.isUserInputEnabled = enable ?: false
    }

    override fun showFragment(index: Int?) {
        val newIndex = index ?: 0
        if ((0 until fragments.size).contains(newIndex)) {
            super.showFragment(newIndex)
            navView.selectedItemId = newIndex
        }
    }

    override fun showFragment(fragment: Fragment) {
        val index = fragments.indexOf(fragment)
        showFragment(index)
    }
}