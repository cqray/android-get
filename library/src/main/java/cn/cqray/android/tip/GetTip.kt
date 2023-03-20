package cn.cqray.android.tip

import cn.cqray.android.Get

/**
 * [Get]全局提示
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
object GetTip {

    /**
     * 显示Tip
     * @param text Tip内容 [CharSequence]
     */
    @JvmStatic
    fun show(text: CharSequence?) = show(
        level = GetTipLevel.INFO,
        text = text,
        init = null,
        callback = null
    )

    /**
     * 显示Tip
     * @param text     Tip内容 [CharSequence]
     * @param callback TIP回调 [GetTipCallback]
     */
    @JvmStatic
    fun show(text: CharSequence?, callback: GetTipCallback?) = show(
        level = GetTipLevel.INFO,
        text = text,
        init = null,
        callback = callback
    )

    /**
     * 显示Tip
     * @param level Tip级别 [GetTipLevel]，默认INFO
     * @param text Tip内容 [CharSequence]
     * @param init Tip属性 [GetTipInit]
     * @param callback TIP回调 [GetTipCallback]
     */
    @JvmStatic
    fun show(
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

