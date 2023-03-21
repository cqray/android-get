package cn.cqray.android.tip

import android.graphics.Color
import androidx.annotation.IntRange
import java.io.Serializable

/**
 * Tip配置属性
 * @author Cqray
 */
class GetTipInit : Serializable {
    /** Tip 适配器  */
    @Transient
    var tipAdapter: GetTipAdapter? = null

    /** 在底部  */
    var atBottom = true

    /** 距离顶部或底部的偏移量，单位DP  */
    var offset = 88f

    /** Tip最小宽度，单位DP  */
    var minWidth = 120f

    /** Tip最大宽度，单位DP  */
    var maxWidth: Float? = null

    /** 文本颜色  */
    var textColor = Color.WHITE

    /** 文本加粗  */
    var textBold = true

    /** 文本大小  */
    var textSize: Float? = null

    /** 时长  */
    @IntRange(from = 0, to = 3500)
    var duration = 2000

    /** 队列长度  */
    var queueCount = 2

    /** 背景圆角  */
    var backgroundRadii: FloatArray? = null

    /** 背景颜色  */
    var backgroundColor = Color.parseColor("#484848")
}