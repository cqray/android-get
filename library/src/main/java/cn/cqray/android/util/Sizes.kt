package cn.cqray.android.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.DimenRes
import cn.cqray.android.R
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils

/**
 * 尺寸工具类
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "SpellCheckingInspection",
    "Unused",
)
object Sizes {

    private val context: Context get() = ContextUtils.get()

    private val dm: DisplayMetrics get() = context.resources.displayMetrics

    val dpScale: Float get() = context.resources.displayMetrics.density

    val spScale: Float get() = context.resources.displayMetrics.scaledDensity

    val ptScale: Float get() = context.resources.displayMetrics.xdpi * (1.0f / 72)

    val inScale: Float get() = context.resources.displayMetrics.xdpi

    val mmScale: Float get() = context.resources.displayMetrics.xdpi * (1.0f / 25.4f)

    @JvmStatic
    fun applyDimension(value: Float, unit: Int) = SizeUtils.applyDimension(value, unit)

    @JvmStatic
    fun any2sp(value: Number, unit: Int) = px2sp(TypedValue.applyDimension(unit, value.toFloat(), dm))

    @JvmStatic
    fun any2px(value: Number, unit: Int) = run {
        val dimen = TypedValue.applyDimension(unit, value.toFloat(), dm)
        if (dimen > 0) (dimen + 0.5).toInt()
        else if (dimen < 0) (dimen - 0.5).toInt()
        else 0
    }

    //===================================================
    //====================PX单位部分======================
    //===================================================

    @JvmStatic
    fun px2dp(pxValue: Number) = pxValue.toFloat() / dpScale

    @JvmStatic
    fun px2sp(pxValue: Number) = pxValue.toFloat() / spScale

    @JvmStatic
    fun px(@DimenRes id: Int): Int = context.resources.getDimensionPixelSize(id)

    @JvmStatic
    fun pxf(@DimenRes id: Int): Float = context.resources.getDimension(id)

    @JvmStatic
    fun pxLine(): Int = px(R.dimen.line)

    @JvmStatic
    fun pxfLine(): Float = pxf(R.dimen.line)

    @JvmStatic
    fun pxLarger(): Int = px(R.dimen.larger)

    @JvmStatic
    fun pxfLarger(): Float = pxf(R.dimen.larger)

    @JvmStatic
    fun pxLarge(): Int = px(R.dimen.large)

    @JvmStatic
    fun pxfLarge(): Float = pxf(R.dimen.large)

    @JvmStatic
    fun pxContent(): Int = px(R.dimen.content)

    @JvmStatic
    fun pxfContent(): Float = pxf(R.dimen.content)

    @JvmStatic
    fun pxSmall(): Int = px(R.dimen.small)

    @JvmStatic
    fun pxfSmall(): Float = pxf(R.dimen.small)

    @JvmStatic
    fun pxSmaller(): Int = px(R.dimen.smaller)

    @JvmStatic
    fun pxfSmaller(): Float = pxf(R.dimen.smaller)

    @JvmStatic
    fun pxDivider(): Int = px(R.dimen.divider)

    @JvmStatic
    fun pxfDivider(): Float = pxf(R.dimen.divider)

    @JvmStatic
    fun pxH1(): Int = px(R.dimen.h1)

    @JvmStatic
    fun pxfH1(): Float = pxf(R.dimen.h1)

    @JvmStatic
    fun pxH2(): Int = px(R.dimen.h2)

    @JvmStatic
    fun pxfH2(): Float = pxf(R.dimen.h2)

    @JvmStatic
    fun pxH3(): Int = px(R.dimen.h3)

    @JvmStatic
    fun pxfH3(): Float = pxf(R.dimen.h3)

    @JvmStatic
    fun pxBody(): Int = px(R.dimen.body)

    @JvmStatic
    fun pxfBody(): Float = pxf(R.dimen.body)

    @JvmStatic
    fun pxCaption(): Int = px(R.dimen.caption)

    @JvmStatic
    fun pxfCaption(): Float = pxf(R.dimen.caption)

    @JvmStatic
    fun pxMin(): Int = px(R.dimen.min)

    @JvmStatic
    fun pxfMin(): Float = pxf(R.dimen.min)

    //===================================================
    //====================DP单位部分======================
    //===================================================

    @JvmStatic
    fun dp2px(dpValue: Number) = SizeUtils.dp2px(dpValue.toFloat())

    @JvmStatic
    fun dp(@DimenRes id: Int): Float = context.resources.getDimension(id) / dpScale

    @JvmStatic
    fun dpLine() = dp(R.dimen.line)

    @JvmStatic
    fun dpLarger() = dp(R.dimen.larger)

    @JvmStatic
    fun dpLarge() = dp(R.dimen.large)

    @JvmStatic
    fun dpContent() = dp(R.dimen.content)

    @JvmStatic
    fun dpSmall() = dp(R.dimen.small)

    @JvmStatic
    fun dpSmaller() = dp(R.dimen.smaller)

    @JvmStatic
    fun dpDivider() = dp(R.dimen.divider)

    //===================================================
    //====================SP单位部分======================
    //===================================================

    @JvmStatic
    fun sp2px(spValue: Number) = SizeUtils.sp2px(spValue.toFloat())

    @JvmStatic
    fun sp(@DimenRes id: Int): Float = context.resources.getDimension(id) / spScale

    @JvmStatic
    fun spH1() = sp(R.dimen.h1)

    @JvmStatic
    fun spH2() = sp(R.dimen.h2)

    @JvmStatic
    fun spH3() = sp(R.dimen.h3)

    @JvmStatic
    fun spBody() = sp(R.dimen.body)

    @JvmStatic
    fun spCaption() = sp(R.dimen.caption)

    @JvmStatic
    fun spMin() = sp(R.dimen.min)
}