package cn.cqray.android.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.DimenRes
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.app.GetManager

/**
 * 尺寸工具类
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object Sizes {

    private val context: Context
        get() {
            val context = GetManager.topActivity
            return context ?: Get.application
        }

    val dpScale: Float get() = Resources.getSystem().displayMetrics.density

    val spScale: Float get() = Resources.getSystem().displayMetrics.scaledDensity

    val ptScale: Float get() = Resources.getSystem().displayMetrics.xdpi * (1.0f / 72)

    val inScale: Float get() = Resources.getSystem().displayMetrics.xdpi

    val mmScale: Float get() = Resources.getSystem().displayMetrics.xdpi * (1.0f / 25.4f)

    @JvmStatic
    fun line(): Float = px(R.dimen.line)

    @JvmStatic
    fun lineDp(): Float = dp(R.dimen.line)

    @JvmStatic
    fun larger(): Float = px(R.dimen.larger)

    @JvmStatic
    fun largerDp(): Float = dp(R.dimen.larger)

    @JvmStatic
    fun large(): Float = px(R.dimen.large)

    @JvmStatic
    fun largeDp(): Float = dp(R.dimen.large)

    @JvmStatic
    fun content(): Float = px(R.dimen.content)

    @JvmStatic
    fun contentDp(): Float = dp(R.dimen.content)

    @JvmStatic
    fun small(): Float = px(R.dimen.small)

    @JvmStatic
    fun smallDp(): Float = dp(R.dimen.small)

    @JvmStatic
    fun smaller(): Float = px(R.dimen.smaller)

    @JvmStatic
    fun smallerDp(): Float = dp(R.dimen.smaller)

    @JvmStatic
    fun divider(): Float = px(R.dimen.divider)

    @JvmStatic
    fun dividerDp(): Float = dp(R.dimen.divider)

    @JvmStatic
    fun h1(): Float = px(R.dimen.h1)

    @JvmStatic
    fun h1Sp(): Float = sp(R.dimen.h1)

    @JvmStatic
    fun h2(): Float = px(R.dimen.h2)

    @JvmStatic
    fun h2Sp(): Float = sp(R.dimen.h2)

    @JvmStatic
    fun h3(): Float = px(R.dimen.h3)

    @JvmStatic
    fun h3Sp(): Float = sp(R.dimen.h3)

    @JvmStatic
    fun body(): Float = px(R.dimen.body)

    @JvmStatic
    fun bodySp(): Float = sp(R.dimen.body)

    @JvmStatic
    fun caption(): Float = px(R.dimen.caption)

    @JvmStatic
    fun captionSp(): Float = sp(R.dimen.caption)

    @JvmStatic
    fun min(): Float = px(R.dimen.min)

    @JvmStatic
    fun minSp(): Float = sp(R.dimen.min)

    @JvmStatic
    fun px(@DimenRes resId: Int): Float = context.resources.getDimension(resId)

    @JvmStatic
    fun sp(@DimenRes resId: Int): Float = px2sp(px(resId))

    @JvmStatic
    fun dp(@DimenRes resId: Int): Float = px2dp(px(resId))

    @JvmStatic
    fun dp2px(dpValue: Float) = (dpValue * dpScale + 0.5f).toInt()

    @JvmStatic
    fun px2dp(pxValue: Float) = pxValue / dpScale

    @JvmStatic
    fun sp2px(spValue: Float) = (spValue * spScale + 0.5f).toInt()

    @JvmStatic
    fun px2sp(pxValue: Float) = pxValue / spScale

    @JvmStatic
    fun applyDimension(value: Float, unit: SizeUnit): Float {
        when (unit.type) {
            TypedValue.COMPLEX_UNIT_PX -> return value
            TypedValue.COMPLEX_UNIT_DIP -> return value * dpScale
            TypedValue.COMPLEX_UNIT_SP -> return value * spScale
            TypedValue.COMPLEX_UNIT_PT -> return value * ptScale
            TypedValue.COMPLEX_UNIT_IN -> return value * inScale
            TypedValue.COMPLEX_UNIT_MM -> return value * mmScale
        }
        return 0F
    }
}