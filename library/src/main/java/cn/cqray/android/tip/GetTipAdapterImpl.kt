package cn.cqray.android.tip

/**
 * Tip适配器实现
 * @author Cqray
 */
internal class GetTipAdapterImpl : GetTipAdapter {
    override fun show(
        tag: Any?,
        level: GetTipLevel?,
        text: CharSequence?,
        init: GetTipInit?,
        callback: GetTipCallback?
    ) {
        // 显示Get框架Tip
        GetTip.show(level, text, init, callback)
    }
}