package cn.cqray.demo.android

import cn.cqray.android.Get
import cn.cqray.android.app.GetFragment
import cn.cqray.android.app.GetViewProvider
//import cn.cqray.android.function.showTip22
import cn.cqray.android.ui.multi.GetMultiProvider

class TestFragment : GetFragment(), GetViewProvider, GetMultiProvider {

    fun ss() {

//        showTip()

//        KeyboardUtils.SS
//        GetIntent()


//        showTip("666", null, ) {
//
//        }
//        timer {
//
//        }

        Get.showTip("6666")

        sss {

        }
    }

    fun sss(function: Function<Unit>) {

    }
}