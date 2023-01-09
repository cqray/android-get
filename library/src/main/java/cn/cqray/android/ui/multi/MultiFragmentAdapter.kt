package cn.cqray.android.ui.multi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Fragment适配器
 * @author Cqray
 */
class MultiFragmentAdapter : FragmentStateAdapter {

    val fragments: MutableList<Fragment> = ArrayList()

    constructor(fragment: Fragment, fragments: List<Fragment>) : super(fragment) {
        this.fragments.addAll(fragments)
    }

    constructor(activity: FragmentActivity, fragments: List<Fragment>) : super(activity) {
        this.fragments.addAll(fragments)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}