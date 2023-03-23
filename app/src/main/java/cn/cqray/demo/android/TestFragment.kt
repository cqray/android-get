package cn.cqray.demo.android

import cn.cqray.android.app.GetFragment
import cn.cqray.android.app.GetIntent
import cn.cqray.android.app.GetViewProvider
import cn.cqray.android.ui.multi.GetMultiProvider
import com.blankj.utilcode.util.KeyboardUtils

class TestFragment : GetFragment(), GetViewProvider, GetMultiProvider {

    fun ss() {

//        KeyboardUtils.SS
        GetIntent()


        showTip("") {

        }
    }
}