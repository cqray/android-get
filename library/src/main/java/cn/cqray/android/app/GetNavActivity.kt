package cn.cqray.android.app

import android.os.Bundle
import cn.cqray.android.R

/**
 * [GetActivity]导航入口界面
 * @author Cqray
 */
@Suppress("unused")
open class GetNavActivity : GetActivity() {

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(R.layout.get_layout_nav)
    }

    fun loadRootFragment(fragmentClass: Class<*>) {
        val intent = GetIntent(fragmentClass)
        navDelegate.loadRootFragment(R.id.get_nav_content, intent)
    }

    fun loadRootFragment(intent: GetIntent) = navDelegate.loadRootFragment(R.id.get_nav_content, intent)
}