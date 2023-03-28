package cn.cqray.android.tip

import cn.cqray.android.app.GetDelegate
import cn.cqray.android.app.GetProvider

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetTipProvider : GetProvider {

    /**
     * 获取并初始化[GetTipDelegate]
     */
    val tipDelegate: GetTipDelegate get() = GetDelegate.get(this, GetTipProvider::class.java)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     */
    fun showTip(text: CharSequence?) = showTip(text, null, null, null)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param hideCallback 隐藏回调
     */
    fun showTip(text: CharSequence?, hideCallback: Function0<Unit>?) = showTip(text, null, hideCallback, null)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>? = null,
        showCallback: Function0<Unit>? = null,
    ) = showTip(text, null, hideCallback, showCallback)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param init 配置属性 [GetTipInit]
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    fun showTip(
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?,
    ) = tipDelegate.showTip(text, init, hideCallback, showCallback)
}