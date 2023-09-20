package cn.cqray.android.app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import cn.cqray.android.R

/**
 * [GetActivity]导航入口界面
 * @author Cqray
 */
@Suppress("unused")
open class GetNavActivity : GetActivity() {

    /** 容器ID **/
    private val containerId = R.id.get_nav_content

    /** [Fragment]内容容器 **/
    private val navContentLayout: FrameLayout by lazy {
        FrameLayout(this).also {
            it.id = R.id.get_nav_content
            it.layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
    }

    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setNativeContentView(navContentLayout)
    }

    fun loadRootFragment(rootClass: Class<*>) = navDelegate.loadRootFragment(containerId, rootClass)

    fun loadRootFragment(intent: GetIntent) = navDelegate.loadRootFragment(containerId, intent)

}