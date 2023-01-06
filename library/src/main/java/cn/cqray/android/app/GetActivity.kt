package cn.cqray.android.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipProvider
import cn.cqray.android.widget.Toolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

open class GetActivity : AppCompatActivity(),
    GetViewProvider,
    GetNavProvider,
    StateProvider,
    GetTipProvider {

    /** 刷新容器 **/
    @JvmField
    var refreshLayout: SmartRefreshLayout? = null

    /** 标题栏 **/
    @JvmField
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreating(savedInstanceState)
    }

    open fun onCreating(savedInstanceState: Bundle?) {}

    final override fun setContentView(layoutResId: Int) = viewDelegate.setGetContentView(layoutResId)

    final override fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    final override fun setContentView(view: View, params: ViewGroup.LayoutParams?) =
        viewDelegate.setGetContentView(view)

    final override fun onBackPressed() {
        // 避免重写，影响内部逻辑
        super.onBackPressed()
    }
}