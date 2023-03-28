package cn.cqray.android.ui.line

import android.graphics.drawable.Drawable
import android.util.TypedValue.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import cn.cqray.android.util.Colors
import cn.cqray.android.util.GetCompat
import cn.cqray.android.util.Sizes
import com.blankj.utilcode.util.CloneUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.*

/**
 * 行项数据基类
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate", "Unchecked_cast", "Unused"
)
open class GetLineItem<T : GetLineItem<T>>(
    override val itemType: Int
) : MultiItemEntity, Serializable {

    /** 标识 **/
    var tag: Any? = null

    /** 行高  */
    var height: Int = Sizes.pxLine()

    /** 外部间隔，左上右下 **/
    val margins = IntArray(4)

    /** 内部间隔，左上右下 **/
    val paddings = IntArray(4)

    /** 分割线高度 **/
    var dividerHeight: Int = 0

    /** 分割线颜色 **/
    @ColorInt
    var dividerColor: Int = Colors.divider()

    /** 分割线间隔，左上右下 **/
    val dividerMargins = IntArray(4)

    /** 背景 **/
    var background: Drawable? = null

    init {
        // 初始化间隔信息
        Sizes.pxContent().let {
            paddings[0] = it
            paddings[2] = it
            dividerMargins[0] = it
            dividerMargins[2] = it
        }
    }

    /**
     * 标识，用以找到对应的项
     * @param tag 标识
     */
    fun tag(tag: Any?) = also { this.tag = tag } as T

    /**
     * 设置高度，默认单位DP
     * @param height 高度为[Int]类型时，会兼容[DimenRes]
     * @param forceInt 高度为[Int]类型时，forceInt为true，会直接使用Int值
     */
    @JvmOverloads
    fun height(
        height: Number,
        forceInt: Boolean = false
    ) = also {
        if (height is Int) this.height = GetCompat.getSize(height, forceInt)
        else height(height.toFloat())
    }

    /**
     * 设置高度，默认单位DP
     * @param height 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun height(
        height: Float,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { this.height = Sizes.any2px(height, unit) } as T

    /**
     * 设置外部间隔，默认单位DP
     * @param margin 高度为[Int]类型时，会兼容[DimenRes]
     * @param forceInt 高度为[Int]类型时，forceInt为true，会直接使用Int值
     */
    @JvmOverloads
    fun margin(
        margin: Number,
        forceInt: Boolean = false
    ) = also {
        if (margin is Int) margin(GetCompat.getSize(margin, forceInt).toFloat(), COMPLEX_UNIT_PX)
        else margin(margin.toFloat())
    }

    /**
     * 设置外部间隔，默认单位DP
     * @param margin 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun margin(
        margin: Float,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { margins.forEachIndexed { i, _ -> margins[i] = Sizes.any2px(margin, unit) } } as T

    fun marginSe(start: Float, end: Float) = marginSe(start, end, COMPLEX_UNIT_DIP)

    fun marginSe(start: Float, end: Float, unit: Int) = also {
        margins[0] = Sizes.any2px(start, unit)
        margins[2] = Sizes.any2px(end, unit)
    } as T

    fun marginTb(top: Float, bottom: Float) = marginTb(top, bottom, COMPLEX_UNIT_DIP)

    fun marginTb(top: Float, bottom: Float, unit: Int) = also {
        margins[1] = Sizes.any2px(top, unit)
        margins[3] = Sizes.any2px(bottom, unit)
    } as T

    /**
     * 设置内部间隔，默认单位DP
     * @param padding 高度为[Int]类型时，会兼容[DimenRes]
     * @param forceInt 高度为[Int]类型时，forceInt为true，会直接使用Int值
     */
    @JvmOverloads
    fun padding(
        padding: Number,
        forceInt: Boolean = false
    ) = also {
        if (padding is Int) padding(GetCompat.getSize(padding, forceInt).toFloat(), COMPLEX_UNIT_PX)
        else height(padding.toFloat())
    }

    /**
     * 设置内部间隔，默认单位DP
     * @param padding 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun padding(
        padding: Float,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { paddings.forEachIndexed { i, _ -> paddings[i] = Sizes.any2px(padding, unit) } } as T

    fun paddingSe(start: Float, end: Float) = paddingSe(start, end, COMPLEX_UNIT_DIP)

    fun paddingSe(start: Float, end: Float, unit: Int) = also {
        paddings[0] = Sizes.any2px(start, unit)
        paddings[2] = Sizes.any2px(end, unit)
    } as T

    fun paddingTb(top: Float, bottom: Float) = paddingTb(top, bottom, COMPLEX_UNIT_DIP)

    fun paddingTb(top: Float, bottom: Float, unit: Int) = also {
        paddings[1] = Sizes.any2px(top, unit)
        paddings[3] = Sizes.any2px(bottom, unit)
    } as T

    fun dividerHeight(height: Float) = dividerHeight(height, COMPLEX_UNIT_DIP)

    fun dividerHeight(height: Float, unit: Int) = also { dividerHeight = Sizes.any2px(height, unit) } as T

    /**
     * 设置分割线颜色，[ColorRes]资源ID或[ColorInt]颜色
     * @param any [ColorRes]资源ID或[ColorInt]颜色
     * @param notId 不是资源ID，则转换成[ColorInt]颜色
     */
    @Suppress("ResourceAsColor")
    @JvmOverloads
    fun dividerColor(
        @ColorRes @ColorInt any: Int,
        notId: Boolean = false
    ) = also { dividerColor = GetCompat.getColor(any, notId) } as T

    fun dividerMarginSE(start: Float, end: Float) = dividerMarginSE(start, end, COMPLEX_UNIT_DIP)

    fun dividerMarginSE(start: Float, end: Float, unit: Int) = also {
        dividerMargins[0] = Sizes.any2px(start, unit)
        dividerMargins[2] = Sizes.any2px(end, unit)
    } as T

    fun dividerMarginTB(top: Float, bottom: Float) = dividerMarginTB(top, bottom, COMPLEX_UNIT_DIP)

    fun dividerMarginTB(top: Float, bottom: Float, unit: Int) = also {
        dividerMargins[1] = Sizes.any2px(top, unit)
        dividerMargins[3] = Sizes.any2px(bottom, unit)
    } as T

    /**
     * 设置背景
     * @param drawable 背景
     */
    fun background(drawable: Drawable?) = also { background = drawable } as T

    /**
     * 设置背景，[DrawableRes]资源ID或[ColorInt]颜色
     * @param any [DrawableRes]资源ID或[ColorInt]颜色
     * @param notId 不是资源文件，则转换成[ColorInt]颜色并获取[Drawable]
     */
    @JvmOverloads
    fun background(
        @DrawableRes @ColorInt any: Int,
        notId: Boolean = false
    ) = also { background = GetCompat.getDrawable(any, notId) } as T

    /**
     * 复制当前项
     */
    fun copy() = CloneUtils.deepClone(this, this.javaClass) as T

    @Suppress("UPPER_BOUND_VIOLATED_WARNING")
    companion object {
        /** 按钮行 **/
        const val BUTTON = -200

        /** 简单图标行 **/
        const val ICON = -199

        /** 文本行 **/
        const val TEXT = -198

        @JvmStatic
        fun button(text: CharSequence? = null) = GetButtonLineItem<GetButtonLineItem<*>>(text)

        @JvmStatic
        fun icon(text: CharSequence? = null) = GetIconLineItem<GetIconLineItem<*>>(text)

        @JvmStatic
        fun text(text: CharSequence? = null) = GetTextLineItem(text)
    }
}