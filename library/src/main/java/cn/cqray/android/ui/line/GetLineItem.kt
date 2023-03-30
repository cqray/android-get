package cn.cqray.android.ui.line

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes
import com.blankj.utilcode.util.CloneUtils
import com.blankj.utilcode.util.Utils
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
    var height = Sizes.pxLine()

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
     * @param height 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun height(
        height: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { this.height = Sizes.any2px(height, unit) } as T

    /**
     * 设置外部间隔，默认单位DP
     * @param margin 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun margin(
        margin: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { margins.forEachIndexed { i, _ -> margins[i] = Sizes.any2px(margin, unit) } } as T

    /**
     * 设置外部左右间隔
     * @param start 左间隔
     * @param end 右间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun marginSe(
        start: Number,
        end: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        margins[0] = Sizes.any2px(start, unit)
        margins[2] = Sizes.any2px(end, unit)
    } as T

    /**
     * 设置外部上下间隔
     * @param top 上间隔
     * @param bottom 下间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun marginTb(
        top: Number,
        bottom: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        margins[1] = Sizes.any2px(top, unit)
        margins[3] = Sizes.any2px(bottom, unit)
    } as T

    /**
     * 设置内部间隔，默认单位DP
     * @param padding 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun padding(
        padding: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { paddings.forEachIndexed { i, _ -> paddings[i] = Sizes.any2px(padding, unit) } } as T

    /**
     * 设置内部左右间隔
     * @param start 左间隔
     * @param end 右间隔
     * @param unit 单位
     */
    fun paddingSe(
        start: Number,
        end: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        paddings[0] = Sizes.any2px(start, unit)
        paddings[2] = Sizes.any2px(end, unit)
    } as T

    /**
     * 设置内部上下间隔
     * @param top 上间隔
     * @param bottom 下间隔
     * @param unit 单位
     */
    fun paddingTb(
        top: Number,
        bottom: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        paddings[1] = Sizes.any2px(top, unit)
        paddings[3] = Sizes.any2px(bottom, unit)
    } as T

    /**
     * 设置分割线高度
     * @param height 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun dividerHeight(
        height: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { dividerHeight = Sizes.any2px(height, unit) } as T

    /**
     * 设置分割线颜色
     * @param color [ColorInt]颜色
     */
    fun dividerColor(@ColorInt color: Int) = also { dividerColor = color } as T

    /**
     * 设置分割线左右间隔
     * @param start 左间隔
     * @param end 右间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun dividerMarginSE(
        start: Number,
        end: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        dividerMargins[0] = Sizes.any2px(start, unit)
        dividerMargins[2] = Sizes.any2px(end, unit)
    } as T

    /**
     * 设置分割线上下间隔
     * @param top 上间隔
     * @param bottom 下间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun dividerMarginTB(
        top: Number,
        bottom: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        dividerMargins[1] = Sizes.any2px(top, unit)
        dividerMargins[3] = Sizes.any2px(bottom, unit)
    } as T

    /**
     * 设置背景
     * @param drawable 背景
     */
    fun background(drawable: Drawable?) = also { background = drawable } as T

    /**
     * 设置背景颜色
     * @param color 背景颜色
     */
    fun backgroundColor(@ColorInt color: Int) = also { background = ColorDrawable(color) } as T

    /**
     * 设置背景，[DrawableRes]资源ID
     * @param id [DrawableRes]资源ID
     */
    fun backgroundResource(@DrawableRes id: Int) = also { background = ContextCompat.getDrawable(Utils.getApp(), id) } as T

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