//package cn.cqray.android.tip
//
//import cn.cqray.android.Get
//import cn.cqray.android.app.GetDelegate
//
///**
// * [Get]提示委托
// * @author Cqray
// */
//@Suppress(
//    "MemberVisibilityCanBePrivate",
//    "Unused"
//)
//class GetTipDelegate(provider: TipProvider) : GetDelegate<TipProvider>(provider) {
//
//    /** 初始化配置 **/
//    var tipInit: TipInit? = null
//
////    /**
////     * 显示Tip
////     * @param text 文本内容 [CharSequence]
////     */
////    fun showTip(text: CharSequence?) = showTip(text, null, null, null)
////
////    /**
////     * 显示Tip
////     * @param text 文本内容 [CharSequence]
////     * @param hideCallback 隐藏回调
////     */
////    fun showTip(
////        text: CharSequence?,
////        hideCallback: Function0<Unit>?,
////    ) = showTip(text, null, hideCallback, null)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param hideCallback 隐藏回调
//     * @param showCallback 显示回调
//     */
//    @JvmOverloads
//    fun showTip(
//        text: CharSequence?,
//        hideCallback: Function0<Unit>? = null,
//        showCallback: Function0<Unit>? = null,
//    ) = showTip(text, null, hideCallback, showCallback)
//
////    /**
////     * 显示Tip
////     * @param text 文本内容 [CharSequence]
////     * @param init 配置属性 [GetTipInit]
////     * @param hideCallback 隐藏回调
////     */
////    fun showTip(
////        text: CharSequence?,
////        init: GetTipInit?,
////        hideCallback: Function0<Unit>?,
////    ) = showTip(text, init, hideCallback, null)
//
//    /**
//     * 显示Tip
//     * @param text 文本内容 [CharSequence]
//     * @param init 配置属性 [TipInit]
//     * @param hideCallback 隐藏回调
//     * @param showCallback 显示回调
//     */
//    @JvmOverloads
//    fun showTip(
//        text: CharSequence?,
//        init: TipInit?,
//        hideCallback: Function0<Unit>?,
//        showCallback: Function0<Unit>? = null,
//    ) {
//        val defInit = tipInit ?: Get.init.tipInit
//        val defAdapter = init?.tipAdapter ?: defInit.tipAdapter
//        val newAdapter = defAdapter ?: GetTipDefAdapter()
//        newAdapter.show(this, text, init, hideCallback, showCallback)
//    }
//}