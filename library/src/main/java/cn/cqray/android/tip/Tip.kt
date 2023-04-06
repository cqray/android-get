package cn.cqray.android.tip

import android.app.Application
import android.os.Handler
import android.os.Looper
import cn.cqray.android.Get
import com.hjq.toast.Toaster
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * 提示
 * @author Cqray
 */
internal object Tip {

    /** TIP **/
    private const val TIP_MESSAGE_WHAT = 0

    /**  **/
    private val tipTaskRef = AtomicReference<TipTask>()

    /** 提示处理[Handler] **/
    private val tipHandler: Handler = Handler(Looper.getMainLooper()) {
        cancelTip()
        true
    }

    /**
     * 取消提示
     */
    private fun cancelTip() {
        tipHandler.removeMessages(0)
        Toaster.cancel()
        if (tipTaskRef.get() != null) {
            tipTaskRef.get().hideCallback?.invoke()
            tipTaskRef.set(null)
        }
    }

    fun init(application: Application) {
        if (!Toaster.isInit()) {
            // 初始化Toaster
            if (!Toaster.isInit()) {
                Toaster.init(application)
            }
        }
    }

    @JvmOverloads
    fun show(
        text: CharSequence?,
        init: TipInit? = null,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) {
        // 取消历史弹窗
        tipHandler.removeMessages(0)
        Toaster.cancel()
        // 显示新的弹窗
        val tipTask = TipTask()
        tipTask.text = text
        tipTask.init = init ?: Get.init.tipInit
        tipTask.hideCallback = hideCallback
        tipTask.showCallback = showCallback
        tipTask.hasShown = true
        Toaster.show(tipTask)
        tipTaskRef.set(tipTask)
        // 弹窗结束回调
        tipHandler.sendEmptyMessageDelayed(TIP_MESSAGE_WHAT, tipTask.init.duration.toLong())
    }
}