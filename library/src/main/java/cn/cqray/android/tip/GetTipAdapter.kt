package cn.cqray.android.tip

import java.io.Serializable

/**
 * Tip适配器
 * @author Cqray
 */
interface GetTipAdapter : Serializable {

    /**
     * 显示Toast
     * @param tag Tip标识
     * @param text Tip内容
     * @param init Tip属性
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    fun show(
        tag: Any?,
        text: CharSequence?,
        init: TipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?,
    )
}