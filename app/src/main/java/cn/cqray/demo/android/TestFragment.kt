package cn.cqray.demo.android

import cn.cqray.android.app.GetFragment
import cn.cqray.android.app.GetViewProvider
import cn.cqray.android.tip.GetTip
import cn.cqray.android.tip.GetTipCallback
import cn.cqray.android.ui.multi.GetMultiProvider

class TestFragment : GetFragment(), GetViewProvider, GetMultiProvider {

    fun ss() {

        GetTip.show("66666", object : GetTipCallback {
            override fun onHide() {
                TODO("Not yet implemented")
            }

        })
    }
}