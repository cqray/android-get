//package cn.cqray.android.tip
//
//import cn.cqray.android.Get
//
///**
// * [Get]全局提示
// * @author Cqray
// */
//@Suppress(
//    "MemberVisibilityCanBePrivate",
//    "Unused"
//)
//object GetTip {
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     */
//    @JvmStatic
//    fun show(text: CharSequence?) = show(text, null, null, null)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param hideCallback 隐藏回调
//     */
//    @JvmStatic
//    fun show(
//        text: CharSequence?,
//        hideCallback: Function0<Unit>?,
//    ) = show(text, null, hideCallback, null)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param hideCallback 隐藏回调
//     * @param showCallback 显示回调
//     */
//    @JvmStatic
//    fun show(
//        text: CharSequence?,
//        hideCallback: Function0<Unit>?,
//        showCallback: Function0<Unit>?,
//    ) = show(text, null, hideCallback, showCallback)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param init 配置属性 [TipInit]
//     * @param hideCallback 隐藏回调
//     */
//    @JvmStatic
//    fun show(
//        text: CharSequence?,
//        init: TipInit?,
//        hideCallback: Function0<Unit>?,
//    ) = show(text, init, hideCallback, null)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param init 配置属性 [TipInit]
//     * @param hideCallback 隐藏回调
//     * @param showCallback 显示回调
//     */
//    @JvmStatic
//    fun show(
//        text: CharSequence?,
//        init: TipInit?,
//        hideCallback: Function0<Unit>?,
//        showCallback: Function0<Unit>?,
//    ) {
//        val defInit = Get.init.tipInit
//        val defAdapter = init?.tipAdapter ?: defInit.tipAdapter
//        val newAdapter = defAdapter ?: GetTipDefAdapter()
//        newAdapter.show(this, text, init, hideCallback, showCallback)
//    }
//}
//
