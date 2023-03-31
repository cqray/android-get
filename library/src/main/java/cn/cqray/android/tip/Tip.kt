package cn.cqray.android.tip

import android.app.Application
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster

internal object Tip {

    fun init(application: Application) {
        if (!Toaster.isInit()) {
            // 初始化Toaster
            if (!Toaster.isInit()) {
                Toaster.init(application, null, TipStyle())
            }
        }
    }

    fun show(text: CharSequence?, init: TipInit? = null) {
        Toaster.show(ToastParams().apply {
            this.text = text
            this.duration = 1500
            this.interceptor = TipLogInterceptor()
        })
    }
}