package cn.cqray.android.tip

import cn.cqray.android.GetDelegate
import cn.cqray.android.GetProvider

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetTipProvider : GetProvider {

    val tipDelegate: GetTipDelegate
        get() = GetDelegate.get(this)

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(text: CharSequence?, callback: GetTipCallback? = null) = tipDelegate.showTip(
        level = null,
        text = text,
        init = null,
        callback = callback
    )

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