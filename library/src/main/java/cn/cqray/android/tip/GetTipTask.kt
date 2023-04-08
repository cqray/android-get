package cn.cqray.android.tip

import android.widget.Toast
import com.hjq.toast.ToastParams

/**
 * [GetTip]任务
 * @author Cqray
 */
internal class GetTipTask @JvmOverloads constructor(
    /** 文本 **/
    text: CharSequence,
    /** Tip属性 **/
    val init: GetTipInit,
    /** 隐藏回调 **/
    var hideCallback: Function0<Unit>? = null,
    /** 显示回调 **/
    var showCallback: Function0<Unit>? = null,
) : ToastParams() {

    init {
        this.text = text
        duration = Toast.LENGTH_LONG
        interceptor = GetTipLogInterceptor()
        style = GetTipStyle(init)
    }
}