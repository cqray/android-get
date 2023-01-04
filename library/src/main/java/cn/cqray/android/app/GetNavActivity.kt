package cn.cqray.android.app

import android.os.Bundle
import cn.cqray.android.R

open class GetNavActivity : GetActivity() {

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        //setNativeContentView(R.layout.starter_navigation_layout)
    }

    fun loadRootFragment(fragmentClass: Class<*>) {
        val intent = GetIntent(fragmentClass)
        navDelegate.loadRootFragment(R.id.starter_content_layout, intent)
    }

    fun loadRootFragment(intent: GetIntent) {
        navDelegate.loadRootFragment(R.id.starter_content_layout, intent)
    }
}