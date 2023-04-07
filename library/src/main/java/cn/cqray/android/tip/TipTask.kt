package cn.cqray.android.tip

import android.widget.Toast
import com.hjq.toast.ToastParams

/**
 * [Tip]任务
 * @author Cqray
 */
internal class TipTask @JvmOverloads constructor(
    /** 文本 **/
    text: CharSequence,
    /** Tip属性 **/
    val init: TipInit,
    /** 隐藏回调 **/
    var hideCallback: Function0<Unit>? = null,
    /** 显示回调 **/
    var showCallback: Function0<Unit>? = null,
) : ToastParams() {

    init {
        this.text = text
        duration = Toast.LENGTH_LONG
        interceptor = TipLogInterceptor()
        style = TipStyle(init)
    }
}