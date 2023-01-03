package cn.cqray.android.tip

/**
 * Tip适配器实现
 * @author Cqray
 */
internal class TipAdapterImpl : TipAdapter {
    override fun show(
        tag: Any?,
        level: TipLevel?,
        text: CharSequence?,
        init: TipInit?,
        callback: GetTipCallback?
    ) {
        // 显示Get框架Tip
        GetTip.show(level, text, init, callback)
    }
}