package cn.cqray.android.tip

import android.app.Application
import android.os.Handler
import android.os.Looper
import cn.cqray.android.Get
import com.blankj.utilcode.util.CloneUtils
import com.hjq.toast.Toaster
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 提示
 * @author Cqray
 */
internal object GetTip {

    /** TIP **/
    private const val TIP_MESSAGE_WHAT = 0

    /**  **/
    private val tipTaskRef = AtomicReference<GetTipTask>()

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

    /**
     * 显示提示
     * @param text 文本
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    @JvmOverloads
    fun show(
        text: CharSequence?,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) = show(text, null, hideCallback, showCallback)

    /**
     * 显示提示
     * @param text 文本
     * @param init 配置
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    @JvmOverloads
    fun show(
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) {
        // 取消历史弹窗
        cancelTip()
        // 新的配置信息
        val tipInit = init?.mergeDefault()
            ?: CloneUtils.deepClone(Get.init.tipInit, GetTipInit::class.java).also {
                // 加载缓存
                it.loadFromLocal()
            }
        // 显示新的弹窗
        val tipTask = GetTipTask(text ?: "", tipInit, hideCallback)
        Toaster.show(tipTask)
        tipTaskRef.set(tipTask)
        Get.runOnUiThreadDelayed({ showCallback?.invoke() })
        // 弹窗结束回调
        val duration = tipTask.init.duration ?: 1500
        val validDuration = max(0, min(abs(duration), 3500))
        tipHandler.sendEmptyMessageDelayed(TIP_MESSAGE_WHAT, validDuration.toLong())
    }
}