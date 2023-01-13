package cn.cqray.android.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipProvider
import cn.cqray.android.widget.Toolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

open class GetFragment : Fragment(), GetViewProvider, GetNavProvider, StateProvider, GetTipProvider {

    /** 刷新容器 **/
    @JvmField
    var refreshLayout: SmartRefreshLayout? = null

    /** 标题栏 **/
    @JvmField
    var toolbar: Toolbar? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreating(savedInstanceState)
        return viewDelegate.rootView ?: super.onCreateView(
            inflater,
            container,
            savedInstanceState
        )
    }

    open fun onCreating(savedInstanceState: Bundle?) {}
}