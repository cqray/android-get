package cn.cqray.android.tip

import android.widget.Toast
import com.hjq.toast.ToastParams

internal class TipTask : ToastParams() {

    /** 隐藏回调 **/
    var hideCallback: Function0<Unit>? = null

    /** 显示回调 **/
    var showCallback: Function0<Unit>? = null

    /** 是否已显示 **/
    var hasShown: Boolean = false

    /** Tip属性  */
    lateinit var init: TipInit

    init {
        duration = Toast.LENGTH_LONG
        interceptor = TipLogInterceptor()
        style = TipStyle()
    }
}