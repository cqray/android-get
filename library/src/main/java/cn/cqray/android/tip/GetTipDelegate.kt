package cn.cqray.android.tip

import cn.cqray.android.Get
import cn.cqray.android.app.GetDelegate

/**
 * [Get]提示委托
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetTipDelegate(provider: GetTipProvider) : GetDelegate<GetTipProvider>(provider) {

    /** 初始化配置 **/
    var tipInit: GetTipInit? = null

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
    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>?,
    ) = showTip(text, null, hideCallback, null)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param hideCallback 隐藏回调
     * @param showCallback 显示回调
     */
    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?,
    ) = showTip(text, null, hideCallback, showCallback)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param init 配置属性 [GetTipInit]
     * @param hideCallback 隐藏回调
     */
    fun showTip(
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?,
    ) = showTip(text, init, hideCallback, null)

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
    ) {
        val defInit = tipInit ?: Get.init.tipInit
        val defAdapter = init?.tipAdapter ?: defInit.tipAdapter
        val newAdapter = defAdapter ?: GetTipDefAdapter()
        newAdapter.show(this, text, init, hideCallback, showCallback)
    }
}