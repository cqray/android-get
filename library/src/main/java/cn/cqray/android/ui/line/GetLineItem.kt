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
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class GetLineItem<T : GetLineItem<T>>(
    override val itemType: Int
) : MultiItemEntity, Serializable {

    /** 标识 **/
    var tag: Any? = null

    /** 行高，单位DP  **/
    var height: Number = Sizes.dpLine()

    /** 外部间隔，左上右下 **/
    val margins = arrayOf<Number>(0, 0, 0, 0)

    /** 内部间隔，左上右下 **/
    val paddings = arrayOf<Number>(0, 0, 0, 0)

    /** 分割线高度 **/
    var dividerHeight: Number = 0

    /** 分割线颜色 **/
    @ColorInt
    var dividerColor: Int = Colors.divider()

    /** 分割线间隔，左上右下 **/
    val dividerMargins = arrayOf<Number>(0, 0, 0, 0)

    /** 背景 **/
    var background: Drawable? = null

    init {
        // 初始化间隔信息
        Sizes.dpContent().let {
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
    ) = also { this.height = Sizes.any2dp(height, unit) } as T

    /**
     * 设置外部间隔，默认单位DP
     * @param margin 高度
     * @param unit 单位
     */
    @JvmOverloads
    fun margin(
        margin: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { margins.forEachIndexed { i, _ -> margins[i] = Sizes.any2dp(margin, unit) } } as T

    /**
     * 设置外部左右间隔
     * @param start 左间隔
     * @param end 右间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun marginH(
        start: Number,
        end: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        margins[0] = Sizes.any2dp(start, unit)
        margins[2] = Sizes.any2dp(end, unit)
    } as T

    /**
     * 设置外部上下间隔
     * @param top 上间隔
     * @param bottom 下间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun marginV(
        top: Number,
        bottom: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        margins[1] = Sizes.any2dp(top, unit)
        margins[3] = Sizes.any2dp(bottom, unit)
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
    ) = also { paddings.forEachIndexed { i, _ -> paddings[i] = Sizes.any2dp(padding, unit) } } as T

    /**
     * 设置内部左右间隔
     * @param start 左间隔
     * @param end 右间隔
     * @param unit 单位
     */
    fun paddingH(
        start: Number,
        end: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        paddings[0] = Sizes.any2dp(start, unit)
        paddings[2] = Sizes.any2dp(end, unit)
    } as T

    /**
     * 设置内部上下间隔
     * @param top 上间隔
     * @param bottom 下间隔
     * @param unit 单位
     */
    fun paddingV(
        top: Number,
        bottom: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        paddings[1] = Sizes.any2dp(top, unit)
        paddings[3] = Sizes.any2dp(bottom, unit)
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
    ) = also { dividerHeight = Sizes.any2dp(height, unit) } as T

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
    fun dividerMarginH(
        start: Number,
        end: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        dividerMargins[0] = Sizes.any2dp(start, unit)
        dividerMargins[2] = Sizes.any2dp(end, unit)
    } as T

    /**
     * 设置分割线上下间隔
     * @param top 上间隔
     * @param bottom 下间隔
     * @param unit 单位
     */
    @JvmOverloads
    fun dividerMarginV(
        top: Number,
        bottom: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also {
        dividerMargins[1] = Sizes.any2dp(top, unit)
        dividerMargins[3] = Sizes.any2dp(bottom, unit)
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
    fun backgroundResource(@DrawableRes id: Int) = also {
        background = ContextCompat.getDrawable(Utils.getApp(), id)
    } as T

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