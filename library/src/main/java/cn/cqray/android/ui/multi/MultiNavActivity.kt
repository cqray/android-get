package cn.cqray.android.ui.multi

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.cqray.android.R
import cn.cqray.android.app.GetActivity
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.GetMultiProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 底部多Fragment界面
 * @author Cqray
 */
class MultiNavActivity : GetActivity(), GetMultiProvider {
    protected var viewPager: ViewPager2? = null
    protected var navigationView: BottomNavigationView? = null
    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(R.layout.get_layout_multi_bottom)
        viewPager = findViewById(R.id.get_nav_content)
        viewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navigationView?.selectedItemId = position
            }
        })
        navigationView = findViewById(R.id.get_nav_view)
        navigationView?.setOnNavigationItemSelectedListener { item: MenuItem ->
            multiDelegate.showFragment(item.order)
            item.isChecked = true
            false
        }
    }

    fun loadMultiFragments(vararg items: MultiItem) {
        resetFragments()
        val menu = navigationView?.menu
        menu?.let {
            it.clear()
            val intents = arrayOfNulls<GetIntent>(items.size)
            for (i in items.indices) {
                intents[i] = items[i].intent
                val ti = items[i]
                it.add(0, i, i, ti.name).setIcon(ti.icon)
            }

        }

//        menu.clear()
//        val intents = arrayOfNulls<GetIntent>(items.size)
//        for (i in items.indices) {
//            intents[i] = items[i].intent
//            val ti = items[i]
//            menu.add(0, i, i, ti.name).setIcon(ti.icon)
//        }
        //        loadMultiFragments(mViewPager, intents);
    }

//    fun setDragEnable(enable: Boolean) {
//        viewPager!!.isUserInputEnabled = enable
//    }

    fun setFragmentDragEnable(enable: Boolean?): () -> Unit = { viewPager?.isUserInputEnabled = enable ?: false }

    override fun showFragment(index: Int?) {
        val newIndex = index ?: 0
        val valid = (0 until fragments.size).contains(newIndex)
        if (valid) {
            multiDelegate.showFragment(newIndex)
            navigationView?.selectedItemId = newIndex
        }
    }

    override fun showFragment(fragment: Fragment) {
        multiDelegate.showFragment(fragment)
        navigationView?.selectedItemId = fragments.indexOf(fragment)
    }

    fun reset() {
        multiDelegate.reset()
    }
}