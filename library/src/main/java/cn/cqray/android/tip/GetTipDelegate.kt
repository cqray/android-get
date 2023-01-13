package cn.cqray.android.tip

import cn.cqray.android.Get
import cn.cqray.android.app.GetDelegate

/**
 * [Get]提示委托
 * @author Cqray
 */
class GetTipDelegate(provider: GetTipProvider): GetDelegate<GetTipProvider>(provider) {

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     */
    fun showTip(text: CharSequence?) = showTip(
        level = null,
        text = text,
        init = null,
        callback = null
    )

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(text: CharSequence?, callback: GetTipCallback? = null) = showTip(
        level = null,
        text = text,
        init = null,
        callback = callback
    )

    /**
     * 显示Tip
     * @param level    提示等级 [GetTipLevel]
     * @param text     文本内容 [CharSequence]
     * @param init 配置属性 [GetTipInit]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(
        level: GetTipLevel?,
        text: CharSequence?,
        init: GetTipInit?,
        callback: GetTipCallback?
    ) {
        val defInit = Get.init.tipInit!!
        val defAdapter = init?.tipAdapter ?: defInit.tipAdapter
        val newAdapter = defAdapter ?: GetTipAdapterImpl()
        newAdapter.show(this, level, text, init, callback)
    }
}