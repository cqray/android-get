package cn.cqray.android.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipProvider
import cn.cqray.android.widget.Toolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

open class GetActivity : AppCompatActivity(), GetViewProvider, GetNavProvider, StateProvider,
    GetTipProvider {

    @JvmField
    var mContentView: View? = null

    @JvmField
    var mRefreshLayout: SmartRefreshLayout? = null

    @JvmField
    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreating(savedInstanceState)
    }

    open fun onCreating(savedInstanceState: Bundle?) {}

    override fun setContentView(layoutResId: Int) = viewDelegate.setGetContentView(layoutResId)

    override fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) =
        viewDelegate.setGetContentView(view)

    override fun addContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.addContentView(view, params)
    }
}