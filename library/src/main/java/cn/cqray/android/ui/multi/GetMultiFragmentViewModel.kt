package cn.cqray.android.ui.multi

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.app.delegate.GetDelegate
import cn.cqray.android.app.delegate.GetMultiDelegate
import cn.cqray.android.app.provider.GetMultiProvider
import cn.cqray.android.databinding.GetLayoutMultiTabBinding
import cn.cqray.android.lifecycle.GetViewModel
import com.flyco.tablayout.listener.OnTabSelectListener

class GetMultiFragmentViewModel(lifecycleOwner: LifecycleOwner) : GetViewModel(lifecycleOwner) {

    var tabAtTop: Boolean = false
        private set

    private var binding: GetLayoutMultiTabBinding

    private val delegate: GetMultiDelegate?

    init {
        val layoutInflater = if (lifecycleOwner is Fragment) {
            lifecycleOwner.layoutInflater
        } else (lifecycleOwner as FragmentActivity).layoutInflater
        // 获取多Fragment委托
        delegate = (lifecycleOwner as? GetMultiProvider)?.multiDelegate
        // 初始化Binding
        binding = GetLayoutMultiTabBinding.inflate(layoutInflater)

        initTabLayout()
    }

    /** 获取根控件 **/
    val rootView: View
        get() = binding.root


    private fun initTabLayout() {
        val listener = object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                delegate?.showFragment(View.NO_ID, position)
            }

            override fun onTabReselect(position: Int) {
                delegate?.showFragment(View.NO_ID, position)
            }
        }
        binding.getTopTab.setOnTabSelectListener(listener)
        binding.getBottomTab.setOnTabSelectListener(listener)
    }

    fun setTabAtTop(tabAtTop: Boolean?) {
        this.tabAtTop = tabAtTop ?: false
    }

//    override fun onCleared() {
//        super.onCleared()
//    }
}