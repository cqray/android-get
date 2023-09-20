package cn.cqray.android.app2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import cn.cqray.android.Get
import cn.cqray.android.handle.GetHandleDelegate
import cn.cqray.android.handle.GetHandleProvider
import cn.cqray.android.tip.GetTipProvider

/**
 * [Get]基础Activity
 * @author Cqray
 */
@Suppress("unused")
open class GetActivity : AppCompatActivity(),
    GetViewProvider,
    GetNavProvider,
    GetTipProvider,
    GetHandleProvider,
    KeyboardProvider {

    override val handleDelegate by lazy { GetHandleDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreating(savedInstanceState)
    }

    open fun onCreating(savedInstanceState: Bundle?) {}

    final override fun setContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    final override fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    final override fun setContentView(view: View, params: ViewGroup.LayoutParams?) =
        viewDelegate.setGetContentView(view)

    /**
     * 避免重写，影响内部逻辑
     */
    final override fun onBackPressed() = super.onBackPressed()

    /**
     * 查找View
     */
    final override fun <T : View> findViewById(@IdRes id: Int): T = super<AppCompatActivity>.findViewById(id)
}