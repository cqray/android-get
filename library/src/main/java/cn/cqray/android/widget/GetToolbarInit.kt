package cn.cqray.android.widget

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import cn.cqray.android.R
import cn.cqray.android.graphics.GetDrawable
import cn.cqray.android.init.BaseInit
import cn.cqray.android.util.Sizes

/**
 * 标题初始化配置
 * @author Cqray
 */
@Suppress("Unused")
class GetToolbarInit : BaseInit() {

    //===============================
    //============背景部分============
    //===============================

    /** 背景 **/
    private val _background = GetDrawable().also { it.set(R.color.colorPrimary) }

    /** 背景 **/
    val background: Drawable? get() = _background.get()

    fun setBackground(drawable: Drawable?) = _background.set(drawable)

    fun setBackground(bitmap: Bitmap?) = _background.set(bitmap)

    @JvmOverloads
    fun setBackground(any: Int, forceColor: Boolean = false) = _background.set(any, forceColor)

    //===============================
    //============常规属性============
    //===============================

    /** 是否启用水波纹 **/
    var ripple: Boolean = true

    /** 阴影高度，单位DP **/
    var elevation: Number = Sizes.dpDivider()

    /** 标题栏高度，单位DP **/
    val height: Number = Sizes.dp(R.dimen.toolbar)

    /** 左右内容间隔，单位DP **/
    var paddingH: Number = Sizes.dpContent()

    //===============================
    //============回退部分============
    //===============================

    /** 回退图片资源 **/
    private val backDrawablePlus = GetDrawable().also { it.set(R.drawable.def_back_material_light) }

    val backIcon: Drawable? get() = backDrawablePlus.get()

    fun setBackIcon(drawable: Drawable?) = backDrawablePlus.set(drawable)

    fun setBackIcon(bitmap: Bitmap?) = backDrawablePlus.set(bitmap)

    @JvmOverloads
    fun setBackIcon(any: Int, forceColor: Boolean = false) = backDrawablePlus.set(any, forceColor)

    /** 回退是否使用水波纹 **/
    var backRipple: Boolean = true

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

    //===============================
    //===========标题部分============
    //===============================

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

    //===============================
    //===========行为部分=============
    //===============================

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

    //===============================
    //===========分割线部分===========
    //===============================

    /** 分割线高度，单位DP **/
    var dividerHeight: Number = Sizes.dpDivider()

    /** 分割线左右间隔，单位DP **/
    var dividerMarginH: Number = 0

    /** 分割线是否显示 **/
    var dividerVisible: Boolean = false

    /** 分割线背景字节数据 **/
    private val _divider = GetDrawable()

    /** 分割线背景 **/
    val dividerDrawable: Drawable? get() = _divider.get()

    fun setDividerDrawable(drawable: Drawable?) = _divider.set(drawable)

    fun setDividerColor(@ColorInt color: Int) = _divider.set(color, true)
}