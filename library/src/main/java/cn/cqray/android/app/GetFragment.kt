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

    lateinit var rootView: View

    lateinit var refreshLayout: SmartRefreshLayout

    lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreating(savedInstanceState)
        return if (viewDelegate.rootView == null) super.onCreateView(
            inflater,
            container,
            savedInstanceState
        ) else viewDelegate.rootView
    }

    open fun onCreating(savedInstanceState: Bundle?) {}
}