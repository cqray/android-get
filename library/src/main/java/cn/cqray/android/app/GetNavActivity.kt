package cn.cqray.android.app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import cn.cqray.android.R

/**
 * [GetActivity]导航入口界面
 * @author Cqray
 */
@Suppress("unused")
open class GetNavActivity : GetActivity() {

    private val contentLayout: FrameLayout by lazy {
        FrameLayout(this).also {
            it.id = R.id.get_nav_content
            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
    }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(contentLayout)
    }

    fun loadRootFragment(fragmentClass: Class<*>) {
        val intent = GetIntent(fragmentClass)
        navDelegate.loadRootFragment(R.id.get_nav_content, intent)
    }

    fun loadRootFragment(intent: GetIntent) = navDelegate.loadRootFragment(R.id.get_nav_content, intent)
}