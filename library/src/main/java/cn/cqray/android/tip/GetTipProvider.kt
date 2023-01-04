package cn.cqray.android.tip

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetTipProvider {

    val tipDelegate: GetTipDelegate
        get() = GetTipDelegate.get(this)

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     */
    fun showTip(text: CharSequence?) = tipDelegate.showTip(text)

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(text: CharSequence?, callback: GetTipCallback? = null) = tipDelegate.showTip(text, callback)

    /**
     * 显示Tip
     * @param level    提示等级 [TipLevel]
     * @param text     文本内容 [CharSequence]
     * @param init 配置属性 [TipInit]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(
        level: TipLevel?,
        text: CharSequence?,
        init: TipInit?,
        callback: GetTipCallback?
    ) = tipDelegate.showTip(level, text, init, callback)
}