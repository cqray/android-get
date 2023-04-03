package cn.cqray.android.tip

import android.graphics.Color
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import cn.cqray.android.init.BaseInit
import cn.cqray.android.util.Sizes

/**
 * Tip配置属性
 * @author Cqray
 */
class TipInit : BaseInit() {

    /** 重心 **/
    var gravity: Int = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM

    /** X值偏移量 **/
    var offsetX: Number = 0

    /** Y值偏移量 **/
    var offsetY: Number = 120

    /** 横向间隔值 **/
    var marginH: Number = 0

    /** 纵向间隔值 **/
    var marginV: Number = 0

    /** Tip最小宽度，单位DP  */
    var minWidth: Number = 120

    /** Tip最大宽度，单位DP  */
    var maxWidth: Number? = null

    /** 文本颜色  */
    @ColorInt
    var textColor: Int = Color.WHITE

    /** 文本样式  */
    var textStyle: Int = 0

    /** 文本大小，单位SP  */
    var textSize: Number = Sizes.spH3()

    /** 时长  */
    @IntRange(from = 0, to = 3500)
    var duration: Int = 1500

    /** 以[Toast.LENGTH_LONG]提示时间的提示 **/
    var longTip: Boolean = false

    /** 背景圆角 **/
    var backgroundRadius: Number = 999

    /** 背景颜色  */
    var backgroundColor: Int = Color.parseColor("#484848")
}