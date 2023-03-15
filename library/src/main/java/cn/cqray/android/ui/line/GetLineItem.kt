package cn.cqray.android.ui.line

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import cn.cqray.android.util.Colors
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.JsonUtils

import cn.cqray.android.util.Sizes
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.*

/**
 * 行项数据基类
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class GetLineItem<T : GetLineItem<T>>(
    override val itemType: Int
) : MultiItemEntity, Serializable {

    /** 标识 **/
    var tag: Any? = null

    /** 行高  */
    var height = Sizes.line()

    /** 外部间隔，左上右下 **/
    val margins = FloatArray(4)

    /** 内部间隔，左上右下 **/
    val paddings = FloatArray(4)

    /** 分割线高度 **/
    var dividerHeight: Float = 0F

    /** 分割线颜色 **/
    var dividerColor: Int = Colors.divider()

    /** 分割线间隔，左上右下 **/
    val dividerMargins = FloatArray(4)

    /** 背景 **/
    var background: Drawable? = null

    init {
        // 初始化间隔信息
        val content = Sizes.content()
        paddings[0] = content
        paddings[2] = content
        dividerMargins[0] = content
        dividerMargins[2] = content
    }

    fun tag(tag: Any?) = also { this.tag = tag } as T

    fun height(height: Float) = height(height, COMPLEX_UNIT_DIP)

    fun height(height: Float, unit: Int) = also { this.height = Sizes.applyDimension(height, unit) } as T

    fun margin(margin: Float) = margin(margin, COMPLEX_UNIT_DIP)

    fun margin(margin: Float, unit: Int) = also {
        margins.forEachIndexed { i, _ ->
            margins[i] = Sizes.applyDimension(margin, unit)
        }
    } as T

    fun marginSE(start: Float, end: Float) = marginSE(start, end, COMPLEX_UNIT_DIP)

    fun marginSE(start: Float, end: Float, unit: Int) = also {
        margins[0] = Sizes.applyDimension(start, unit)
        margins[2] = Sizes.applyDimension(end, unit)
    } as T

    fun marginTB(top: Float, bottom: Float) = marginTB(top, bottom, COMPLEX_UNIT_DIP)

    fun marginTB(top: Float, bottom: Float, unit: Int) = also {
        margins[1] = Sizes.applyDimension(top, unit)
        margins[3] = Sizes.applyDimension(bottom, unit)
    } as T

    fun padding(padding: Float) = padding(padding, COMPLEX_UNIT_DIP)

    fun padding(padding: Float, unit: Int) = also {
        paddings.forEachIndexed { i, _ ->
            paddings[i] = Sizes.applyDimension(padding, unit)
        }
    } as T

    fun paddingSE(start: Float, end: Float) = paddingSE(start, end, COMPLEX_UNIT_DIP)

    fun paddingSE(start: Float, end: Float, unit: Int) = also {
        paddings[0] = Sizes.applyDimension(start, unit)
        paddings[2] = Sizes.applyDimension(end, unit)
    } as T

    fun paddingTB(top: Float, bottom: Float) = paddingTB(top, bottom, COMPLEX_UNIT_DIP)

    fun paddingTB(top: Float, bottom: Float, unit: Int) = also {
        paddings[1] = Sizes.applyDimension(top, unit)
        paddings[3] = Sizes.applyDimension(bottom, unit)
    } as T

    fun dividerHeight(height: Float) = dividerHeight(height, COMPLEX_UNIT_DIP)

    fun dividerHeight(height: Float, unit: Int) = also { dividerHeight = Sizes.applyDimension(height, unit) } as T

    fun dividerColor(color: Int) = also { dividerColor = color } as T

    fun dividerMarginSE(start: Float, end: Float) = dividerMarginSE(start, end, COMPLEX_UNIT_DIP)

    fun dividerMarginSE(start: Float, end: Float, unit: Int) = also {
        dividerMargins[0] = Sizes.applyDimension(start, unit)
        dividerMargins[2] = Sizes.applyDimension(end, unit)
    } as T

    fun dividerMarginTB(top: Float, bottom: Float) = dividerMarginTB(top, bottom, COMPLEX_UNIT_DIP)

    fun dividerMarginTB(top: Float, bottom: Float, unit: Int) = also {
        dividerMargins[1] = Sizes.applyDimension(top, unit)
        dividerMargins[3] = Sizes.applyDimension(bottom, unit)
    } as T

    fun background(drawable: Drawable?) = also { background = drawable } as T

    fun backgroundColor(@ColorInt color: Int) = also { background = ColorDrawable(color) } as T

    fun backgroundRes(@DrawableRes id: Int) = also { background = ContextUtils.getDrawable(id) } as T

    fun copy() = JsonUtils.deepClone(this, this.javaClass) as T

    @Suppress("UPPER_BOUND_VIOLATED_WARNING")
    companion object {
        /** 按钮行 **/
        const val BUTTON = -200

        /** 简单图标行 **/
        const val ICON = -199

        /** 文本行 **/
        const val TEXT = -198

        fun button(text: CharSequence? = null) = GetButtonLineItem<GetButtonLineItem<*>>(text)

        fun icon(text: CharSequence? = null) = GetIconLineItem<GetIconLineItem<*>>(text)

        fun text(text: CharSequence? = null) = GetTextLineItem(text)
    }
}