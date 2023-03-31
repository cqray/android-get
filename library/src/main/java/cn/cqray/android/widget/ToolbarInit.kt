package cn.cqray.android.widget

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import cn.cqray.android.R
import cn.cqray.android.init.BaseInit
import cn.cqray.android.util.Sizes

/**
 * 标题初始化配置
 * @author Cqray
 */
class ToolbarInit : BaseInit() {

    /** 背景资源 **/
    @DrawableRes
    var backgroundResource: Int? = R.color.colorPrimary
        get() {
            if (field != null) backgroundColor = null
            return field
        }

    /** 背景颜色 **/
    @ColorInt
    var backgroundColor: Int? = null
        set(value) {
            field = value
            backgroundResource = null
        }

    /** 是否启用水波纹 **/
    var ripple: Boolean = true

    /** 阴影高度，单位DP **/
    var elevation: Number = Sizes.dpDivider()

    /** 标题栏高度，单位DP **/
    val height: Number = Sizes.dp(R.dimen.toolbar)

    /** 左右内容间隔，单位DP **/
    var paddingSE: Number = Sizes.dpContent()

    /** 回退是否使用水波纹 **/
    var backRipple: Boolean = true

    /** 回退按钮图片资源 **/
    @DrawableRes
    var backIcon: Int? = R.drawable.def_back_material_light

    /** 回退图片与文本间的间隔，单位DP **/
    var backIconSpace: Number = Sizes.dpSmall()

    /** 回退按钮图片颜色 **/
    @ColorInt
    var backIconTintColor: Int? = null

    /** 回退文本 **/
    var backText: String? = null

    /** 回退文本颜色 **/
    @ColorInt
    var backTextColor: Int = Color.WHITE

    /** 回退文本大小，单位DP **/
    var backTextSize: Number = Sizes.spH3()

    /** 回退文本样式 **/
    var backTextStyle: Int = 0

    /** 标题是否居中 **/
    var titleCenter: Boolean = false

    /** 标题左右间隔，单位DP **/
    var titleSpace: Number = Sizes.dpContent()

    /** 标题文本颜色 **/
    @ColorInt
    var titleTextColor: Int = Color.WHITE

    /** 标题文本大小，单位DP **/
    var titleTextSize: Number = Sizes.spH2()

    /** 标题文本样式 **/
    var titleTextStyle: Int = 0

    /** 行为控件是否使用水波纹 **/
    var actionRipple: Boolean = true

    /** 行为控件间的间隔，单位DP **/
    var actionSpace: Number = Sizes.dpSmall()

    /** 行为控件间文本颜色 **/
    @ColorInt
    var actionTextColor: Int = Color.WHITE

    /** 行为控件间文本大小，单位DP **/
    var actionTextSize: Number = Sizes.spH3()

    /** 行为控件间文本样式 **/
    var actionTextStyle: Int = 0

    /** 分割线图片 **/
    @DrawableRes
    var dividerResource: Int? = R.color.divider
        get() {
            if (field != null) dividerColor = null
            return field
        }

    /** 分割线颜色 **/
    @ColorInt
    var dividerColor: Int? = null
        set(value) {
            field = value
            dividerResource = null
        }

    /** 分割线高度，单位DP **/
    var dividerHeight: Number = Sizes.dpDivider()

    /** 分割线左右间隔，单位DP **/
    var dividerMargin: Number = 0

    /** 分割线是否显示 **/
    var dividerVisible: Boolean = false
}