package cn.cqray.android.ui.multi

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.app.GetActivity
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.databinding.GetLayoutMultiTabBinding
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener

/**
 * 底部多Fragment界面
 * @author Cqray
 */
open class MultiTabActivity : GetActivity(), GetMultiProvider {


    //val tabLayout: CommonTabLayout

    private var tabAtTop = false

    private lateinit var tabBinding: GetLayoutMultiTabBinding

    private val unusedTabLayout: CommonTabLayout
        get() {
            return if (tabAtTop) tabBinding.getBottomTab
            else tabBinding.getTopTab
        }

    val tabLayout: CommonTabLayout
        get() {
            return if (tabAtTop) tabBinding.getTopTab
            else tabBinding.getBottomTab
        }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        tabBinding = GetLayoutMultiTabBinding.inflate(layoutInflater)
        setNativeContentView(tabBinding.root)

        tabBinding.getNavContent.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.currentTab = position
            }
        })

        tabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                //TODO("Not yet implemented")
                multiDelegate.showFragment(View.NO_ID, position)
            }

            override fun onTabReselect(position: Int) {
                //TODO("Not yet implemented")
            }

        })

        unusedTabLayout.visibility = View.GONE
    }

    fun loadMultiFragments(vararg items: MultiItem2) {
        val entries = ArrayList<CustomTabEntity>();
        items.forEach {
            val entry = object : CustomTabEntity {
                override fun getTabTitle(): String {
                    return it.name ?: ""
                }

                override fun getTabSelectedIcon(): Int {
                    //TODO("Not yet implemented")
                    return it.selectIcon ?: 0
                }

                override fun getTabUnselectedIcon(): Int {
                    return it.unselectIcon ?: 0
                    //TODO("Not yet implemented")
                }
            }
            entries.add(entry)
        }
        tabLayout.setTabData(entries)


//        removeFragments()
//        val menu = navView.menu
//        menu.clear()
//        val intents = Array<GetIntent>(items.size) { items[it].intent }
//        for (i in items.indices) {
//            intents[i] = items[i].intent
//            val ti = items[i]
//            menu.add(0, i, i, ti.name).setIcon(ti.icon)
//        }
//        viewPager.offscreenPageLimit = items.size


        multiDelegate.loadMultiFragments(tabBinding.getNavContent, arrayOf(*items))
    }
}