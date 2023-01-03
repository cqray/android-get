package cn.cqray.android.app2

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

    @JvmField
    var mContentView: View? = null

    @JvmField
    var mRefreshLayout: SmartRefreshLayout? = null

    @JvmField
    var mToolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreating(savedInstanceState)
        return if (viewDelegate.mRootView == null) super.onCreateView(
            inflater,
            container,
            savedInstanceState
        ) else viewDelegate.mRootView
    }

    open fun onCreating(savedInstanceState: Bundle?) {}
}