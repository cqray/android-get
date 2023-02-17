package cn.cqray.android.widget

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.util.ContextUtils

/**
 * 标题初始化配置
 * @author Cqray
 */
@Keep
class ToolbarInit {

    /** 背景 **/
    var background: Drawable? = null

    /** 是否启用水波纹 **/
    var ripple: Boolean? = null

    /** 阴影高度 **/
    var elevation: Float? = null

    /** 左右内容间隔 **/
    var contentPadding: Float? = null

    /** 回退是否使用水波纹 **/
    var backRipple: Boolean? = null

    /** 回退按钮图片资源 **/
    @DrawableRes
    var backIcon: Int? = R.drawable.def_back_material_light

    /** 回退图片与文本间的间隔 **/
    var backIconSpace: Float? = null

    /** 回退按钮图片颜色 **/
    @ColorInt
    var backIconTintColor: Int? = null

    /** 回退文本 **/
    var backText: String? = null

    /** 回退文本颜色 **/
    @ColorInt
    var backTextColor: Int? = null

    /** 回退文本大小 **/
    var backTextSize: Float? = null

    /** 回退文本样式 **/
    var backTextTypeface: Typeface? = null

    /** 标题是否居中 **/
    var titleCenter: Boolean? = null

    /** 标题左右间隔 **/
    var titleSpace: Float? = null

    /** 标题文本颜色 **/
    @ColorInt
    var titleTextColor: Int? = null

    /** 标题文本大小 **/
    var titleTextSize: Float? = null

    /** 标题文本样式 **/
    var titleTextTypeFace: Typeface? = null

    /** 行为控件是否使用水波纹 **/
    var actionRipple: Boolean? = null

    /** 行为控件间的间隔 **/
    var actionSpace: Float? = null

    /** 行为控件间文本颜色 **/
    @ColorInt
    var actionTextColor: Int? = null

    /** 行为控件间文本大小 **/
    var actionTextSize: Float? = null

    /** 行为控件间文本样式 **/
    var actionTextTypeface: Typeface? = null

    /** 分割线图片 **/
    var dividerDrawable: Drawable? = null

    /** 分割线高度 **/
    var dividerHeight: Float? = null

    /** 分割线左右间隔 **/
    var dividerMargin: Float? = null

    /** 分割线是否显示 **/
    var dividerVisible: Boolean? = null

}