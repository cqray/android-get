package cn.cqray.android.tip

import cn.cqray.android.app.GetDelegate
import cn.cqray.android.app.GetProvider

/**
 * Tip提供者
 * @author Cqray
 */
@Suppress("Deprecation")
@JvmDefaultWithoutCompatibility
interface GetTipProvider : GetProvider {

    /**
     * 获取并初始化[GetTipDelegate]
     */
    val tipDelegate: GetTipDelegate
        get() = GetDelegate.get(this, GetTipProvider::class.java)

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     */
    @JvmDefault
    fun showTip(text: CharSequence?) = tipDelegate.showTip(text)

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     * @param callback 结束回调 [GetTipCallback]
     */
    @JvmDefault
    fun showTip(text: CharSequence?, callback: GetTipCallback? = null) = tipDelegate.showTip(text, callback)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param init 配置属性 [GetTipInit]
     * @param callback 结束回调 [GetTipCallback]
     */
    @JvmDefault
    fun showTip(
        text: CharSequence?,
        init: GetTipInit?,
        callback: GetTipCallback? = null
    ) = tipDelegate.showTip(null, text, init, callback)

    /**
     * 显示Tip
     * @param level 提示等级 [GetTipLevel]
     * @param text 文本内容 [CharSequence]
     * @param init 配置属性 [GetTipInit]
     * @param callback 结束回调 [GetTipCallback]
     */
    @JvmDefault
    fun showTip(
        level: GetTipLevel?,
        text: CharSequence?,
        init: GetTipInit?,
        callback: GetTipCallback?
    ) = tipDelegate.showTip(level, text, init, callback)
}