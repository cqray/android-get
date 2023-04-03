package cn.cqray.android.tip

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import cn.cqray.android.Get
import cn.cqray.android.util.Sizes
import com.blankj.utilcode.util.CloneUtils
import com.hjq.toast.config.IToastStyle

/**
 * [Tip]样式
 * @author Cqray
 */
class TipStyle : IToastStyle<View> {

    private val tipInit: TipInit by lazy {
        val init = Get.init.tipInit
        init.loadFromLocal()
        CloneUtils.deepClone(init, TipInit::class.java)
    }

    override fun createView(context: Context?): View {
        val view = TextView(context ?: Get.context)
        initTipText(view)
        initTipBackground(view)
        return view
    }

    /** 初始化提示文本 **/
    private fun initTipText(view: TextView) {
        // 设置Tip样式
        val size = Sizes.dp2px(tipInit.textSize).toFloat()
        val lrPadding = (size * 3 / 2).toInt()
        val tbPadding = (size * 2 / 3).toInt()
        // 设置Tip宽度限制
        view.minimumWidth = Sizes.dp2px(tipInit.minWidth)
        tipInit.maxWidth?.let { view.maxWidth = Sizes.dp2px(it) }
        view.typeface = Typeface.defaultFromStyle(tipInit.textStyle)
        view.setTextColor(tipInit.textColor)
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        view.setPadding(lrPadding, tbPadding, lrPadding, tbPadding)
    }

    /** 初始化提示背景 **/
    private fun initTipBackground(view: TextView) {
        val radii = FloatArray(8)
        radii.forEachIndexed { i, _ -> radii[i] = Sizes.dp2px(tipInit.backgroundRadius).toFloat() }
        // 构建背景Drawable
        val background = GradientDrawable()
        background.setColor(tipInit.backgroundColor)
        background.cornerRadii = radii
        // 设置背景
        ViewCompat.setBackground(view, background)
    }

    override fun getGravity() = tipInit.gravity

    override fun getYOffset() = Sizes.dp2px(tipInit.offsetY)

    override fun getXOffset() = Sizes.dp2px(tipInit.offsetX)

    override fun getHorizontalMargin() = Sizes.dp2px(tipInit.marginH).toFloat()

    override fun getVerticalMargin() = Sizes.dp2px(tipInit.marginV).toFloat()
}