package cn.cqray.android.tip

/**
 * Tip任务
 * @author Cqray
 */
internal class TipTask2 {
    /** Tip等级  */
    var level: TipLevel? = null

    /** 文本  */
    var text: CharSequence? = null

    /** 结束回调  */
    var callback: GetTipCallback? = null

    /** Tip属性  */
    var init: TipInit? = null
}