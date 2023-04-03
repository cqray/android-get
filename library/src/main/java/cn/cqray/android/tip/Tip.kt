package cn.cqray.android.tip

import android.app.Application
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster

/**
 * 提示
 * @author Cqray
 */
internal object Tip {

    fun init(application: Application) {
        if (!Toaster.isInit()) {
            // 初始化Toaster
            if (!Toaster.isInit()) {
                Toaster.init(application)
            }
        }
    }

    fun show(text: CharSequence?, init: TipInit? = null) {
        Toaster.show(ToastParams().apply {
            this.text = text
            this.duration = 1500
            this.interceptor = TipLogInterceptor()
            this.style = TipStyle()
        })
    }
}