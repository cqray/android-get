package cn.cqray.android.ui.multi

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.cqray.android.R
import cn.cqray.android.app.GetActivity
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.provider.GetMultiProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 * 底部多Fragment界面
 * @author Cqray
 */
class MultiTabActivity : GetActivity(), GetMultiProvider {

    @Suppress("unused")
    lateinit var viewPager: ViewPager2
        private set

    lateinit var tabLayout: TabLayout
        private set

    fun requireViewPager(): ViewPager2 = viewPager!!

    fun requireTabLayout(): TabLayout = tabLayout

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(R.layout.get_layout_multi_top)
        viewPager = findViewById(R.id.get_nav_content)
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position), true)
            }
        })
        tabLayout = findViewById(R.id.get_nav_tab)
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                multiDelegate.showFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun loadMultiFragments(vararg items: MultiItem) {
        resetFragments()
        tabLayout.removeAllTabs()
        val intents = arrayOfNulls<GetIntent>(items.size)
        for (i in items.indices) {
            intents[i] = items[i].intent
            val ti = items[i]
            val tab = tabLayout.newTab()
            if (ti.icon != 0) {
                tab.setIcon(ti.icon)
            }
            tab.text = ti.name
            tabLayout.addTab(tab)
        }
//        multiDelegate.loadMultiFragments(viewPager, intents)
    }

    //    public void addFragment(@NonNull MultiItem item) {
    //        TabLayout.Tab tab = tabLayout.newTab();
    //        if (item.getIcon() != 0) {
    //            tab.setIcon(item.getIcon());
    //        }
    //        tab.setText(item.getName());
    //        tabLayout.addTab(tab);
    //        //getMultiDelegate().addFragment(item.getIntent());
    //    }
    //
    //    public void removeFragment(int position) {
    //        mTabLayout.removeTabAt(position);
    //        mMultiDelegate.removeFragment(position);
    //    }
    //    public void setDragEnable(boolean enable) {
    //        mViewPager.setUserInputEnabled(enable);
    //    }

    override fun showFragment(index: Int?) {
        val newIndex = index ?: 0
        val valid = (0 until fragments.size).contains(newIndex)
        if (valid) {
            multiDelegate.showFragment(newIndex)
            tabLayout.selectTab(tabLayout.getTabAt(newIndex), true)
            //tabLayout.let { it.selectTab(it.getTabAt(newIndex), true) }
        }
    }

    override fun showFragment(fragment: Fragment) {
        val index = fragments.indexOf(fragment)
        showFragment(index)
    }
}