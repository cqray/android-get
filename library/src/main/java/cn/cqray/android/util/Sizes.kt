package cn.cqray.android.util

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DimenRes
import cn.cqray.android.Get
import cn.cqray.android.R
import com.blankj.utilcode.util.SizeUtils

/**
 * 尺寸工具类
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
object Sizes {

    private val context: Context
        get() {
            val context = Get.topActivity
            return context ?: Get.application
        }

    val dpScale: Float get() = Resources.getSystem().displayMetrics.density

    val spScale: Float get() = Resources.getSystem().displayMetrics.scaledDensity

    val ptScale: Float get() = Resources.getSystem().displayMetrics.xdpi * (1.0f / 72)

    val inScale: Float get() = Resources.getSystem().displayMetrics.xdpi

    val mmScale: Float get() = Resources.getSystem().displayMetrics.xdpi * (1.0f / 25.4f)

    @JvmStatic
    fun line(): Int = px(R.dimen.line)

    @JvmStatic
    fun lineDp(): Float = dp(R.dimen.line)

    @JvmStatic
    fun larger(): Int = px(R.dimen.larger)

    @JvmStatic
    fun largerDp(): Float = dp(R.dimen.larger)

    @JvmStatic
    fun large(): Int = px(R.dimen.large)

    @JvmStatic
    fun largeDp(): Float = dp(R.dimen.large)

    @JvmStatic
    fun content(): Int = px(R.dimen.content)

    @JvmStatic
    fun contentDp(): Float = dp(R.dimen.content)

    @JvmStatic
    fun small(): Int = px(R.dimen.small)

    @JvmStatic
    fun smallDp(): Float = dp(R.dimen.small)

    @JvmStatic
    fun smaller(): Int = px(R.dimen.smaller)

    @JvmStatic
    fun smallerDp(): Float = dp(R.dimen.smaller)

    @JvmStatic
    fun divider(): Int = px(R.dimen.divider)

    @JvmStatic
    fun dividerDp(): Float = dp(R.dimen.divider)

    @JvmStatic
    fun h1(): Int = px(R.dimen.h1)

    @JvmStatic
    fun h1Sp(): Float = sp(R.dimen.h1)

    @JvmStatic
    fun h2(): Int = px(R.dimen.h2)

    @JvmStatic
    fun h2Sp(): Float = sp(R.dimen.h2)

    @JvmStatic
    fun h3(): Int = px(R.dimen.h3)

    @JvmStatic
    fun h3Sp(): Float = sp(R.dimen.h3)

    @JvmStatic
    fun body(): Int = px(R.dimen.body)

    @JvmStatic
    fun bodySp(): Float = sp(R.dimen.body)

    @JvmStatic
    fun caption(): Int = px(R.dimen.caption)

    @JvmStatic
    fun captionSp(): Float = sp(R.dimen.caption)

    @JvmStatic
    fun min(): Int = px(R.dimen.min)

    @JvmStatic
    fun minSp(): Float = sp(R.dimen.min)

    @JvmStatic
    fun px(@DimenRes id: Int): Int = context.resources.getDimensionPixelSize(id)

    @JvmStatic
    fun sp(@DimenRes id: Int): Float = context.resources.getDimension(id) / spScale

    @JvmStatic
    fun dp(@DimenRes id: Int): Float = context.resources.getDimension(id) / dpScale

    @JvmStatic
    fun px2dp(pxValue: Float) = SizeUtils.px2dp(pxValue)

    @JvmStatic
    fun px2sp(pxValue: Float) = SizeUtils.px2sp(pxValue)

    @JvmStatic
    fun sp2px(spValue: Float) = SizeUtils.sp2px(spValue)

    @JvmStatic
    fun dp2px(dpValue: Float) = SizeUtils.dp2px(dpValue)


//    @JvmStatic
//    fun applyDimension(value: Float, unit: SizeUnit): Float {
//        when (unit.type) {
//            TypedValue.COMPLEX_UNIT_PX -> return value
//            TypedValue.COMPLEX_UNIT_DIP -> return value * dpScale
//            TypedValue.COMPLEX_UNIT_SP -> return value * spScale
//            TypedValue.COMPLEX_UNIT_PT -> return value * ptScale
//            TypedValue.COMPLEX_UNIT_IN -> return value * inScale
//            TypedValue.COMPLEX_UNIT_MM -> return value * mmScale
//        }
//        return 0F
//    }

    @JvmStatic
    fun applyDimension(value: Float, unit: Int) = SizeUtils.applyDimension(value, unit)


//    @JvmStatic
//    fun applyDimension(value: Float, unit: Int): Float {
//        when (unit) {
//            TypedValue.COMPLEX_UNIT_PX -> return value
//            TypedValue.COMPLEX_UNIT_DIP -> return value * dpScale
//            TypedValue.COMPLEX_UNIT_SP -> return value * spScale
//            TypedValue.COMPLEX_UNIT_PT -> return value * ptScale
//            TypedValue.COMPLEX_UNIT_IN -> return value * inScale
//            TypedValue.COMPLEX_UNIT_MM -> return value * mmScale
//        }
//        return 0F
//    }
}