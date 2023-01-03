package cn.cqray.android.tip

import java.io.Serializable

/**
 * Tip适配器
 * @author Cqray
 */
interface TipAdapter : Serializable {
    /**
     * 显示Toast
     * @param tag      Tip标识
     * @param level    Tip级别
     * @param text     Tip内容
     * @param init Tip属性
     * @param callback Tip回调
     */
    fun show(
        tag: Any?,
        level: TipLevel?,
        text: CharSequence?,
        init: TipInit?,
        callback: GetTipCallback?
    )
}