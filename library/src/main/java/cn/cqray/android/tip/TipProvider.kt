package cn.cqray.android.tip

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface TipProvider {

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     */
    fun showTip(text: CharSequence?) = Tip.show(text, null)

//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param hideCallback 隐藏回调
//     */
//    fun showTip(text: CharSequence?, hideCallback: Function0<Unit>?) = showTip(text, null, hideCallback, null)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param hideCallback 隐藏回调
//     * @param showCallback 显示回调
//     */
//    fun showTip(
//        text: CharSequence?,
//        hideCallback: Function0<Unit>? = null,
//        showCallback: Function0<Unit>? = null,
//    ) = showTip(text, null, hideCallback, showCallback)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param init 配置属性 [TipInit]
//     * @param hideCallback 隐藏回调
//     * @param showCallback 显示回调
//     */
//    fun showTip(
//        text: CharSequence?,
//        init: TipInit?,
//        hideCallback: Function0<Unit>?,
//        showCallback: Function0<Unit>?,
//    ) = tipDelegate.showTip(text, init, hideCallback, showCallback)
}