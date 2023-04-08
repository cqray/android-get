package cn.cqray.android.tip

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import cn.cqray.android.Get
import cn.cqray.android.util.Sizes
import com.blankj.utilcode.util.ScreenUtils
import com.hjq.toast.config.IToastStyle

/**
 * [GetTip]样式
 * @author Cqray
 */
internal class GetTipStyle(private val tipInit: GetTipInit) : IToastStyle<View> {

    override fun createView(context: Context?): View {
        val view = TextView(context ?: Get.context)
        initTipText(view)
        initTipBackground(view)
        return view
    }

    /** 初始化提示文本 **/
    private fun initTipText(view: TextView) {
        // 设置Tip样式
        val size = Sizes.dp2px(tipInit.textSize ?: Sizes.spH3()).toFloat()
        val lrPadding = (size * 3 / 2).toInt()
        val tbPadding = (size * 2 / 3).toInt()
        // 设置Tip宽度限制
        view.minimumWidth = Sizes.dp2px(tipInit.minWidth ?: 120)
        tipInit.maxWidth?.let { view.maxWidth = Sizes.dp2px(it) }
        view.typeface = Typeface.defaultFromStyle(tipInit.textStyle ?: 0)
        view.setTextColor(tipInit.textColor ?: Color.WHITE)
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        view.setPadding(lrPadding, tbPadding, lrPadding, tbPadding)
    }

    /** 初始化提示背景 **/
    private fun initTipBackground(view: TextView) {
        val radii = FloatArray(8)
        radii.forEachIndexed { i, _ -> radii[i] = Sizes.dp2px(tipInit.backgroundRadius ?: 999).toFloat() }
        // 构建背景Drawable
        val background = GradientDrawable()
        background.setColor(tipInit.backgroundColor ?: Color.parseColor("#484848"))
        background.cornerRadii = radii
        // 设置背景
        ViewCompat.setBackground(view, background)
    }

    override fun getGravity() = tipInit.gravity ?: (Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)

    override fun getYOffset() = Sizes.dp2px(tipInit.offsetY ?: Sizes.px2dp(ScreenUtils.getAppScreenHeight() / 4))

    override fun getXOffset() = Sizes.dp2px(tipInit.offsetX ?: 0)

    override fun getHorizontalMargin() = Sizes.dp2px(tipInit.marginH ?: 0).toFloat()

    override fun getVerticalMargin() = Sizes.dp2px(tipInit.marginV ?: 0).toFloat()
}