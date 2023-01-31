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

@Keep
class ToolbarInit {

    var ripple: Boolean? = null

    var elevation: Float? = null

    var contentPadding: Float? = null

    var backRipple: Boolean? = null
    @DrawableRes
    var backIcon: Int? = R.drawable.def_back_material_light
    var backIconSpace: Float? = null
    @ColorInt
    var backIconTintColor: Int? = null
    var backText: String? = null
    @ColorInt
    var backTextColor: Int? = null
    var backTextSize: Float? = null
    var backTextTypeface: Typeface? = null

    var titleCenter: Boolean? = null
    var titleSpace: Float? = null
    @ColorInt
    var titleTextColor: Int? = null
    var titleTextSize: Float? = null
    var titleTextTypeFace: Typeface? = null

    var actionRipple: Boolean? = null
    var actionSpace: Float? = null
    @ColorInt
    var actionTextColor: Int? = null
    var actionTextSize: Float? = null
    var actionTextTypeface: Typeface? = null

    var dividerDrawable: Drawable? = null
    var dividerHeight: Float? = null
    var dividerMargin: Float? = null
    var dividerVisible: Boolean? = null
//    var titleTypeFace: Boolean? = null

    //            mToolbar!!.setUseRipple(strategy.isToolbarUserRipple).background =
//                strategy.toolbarBackground
//            // 设置标题栏标题属性
//            mToolbar!!.setTitleCenter(strategy.isToolbarTitleCenter)
//                .setTitleTextColor(strategy.toolbarTitleTextColor)
//                .setTitleTextSize(strategy.toolbarTitleTextSize)
//                .setTitleTypeface(strategy.toolbarTitleTypeface)
//                .setTitleSpace(strategy.toolbarTitleSpace)
//            // 设置标题栏返回控件属性
//            mToolbar!!.setBackText(strategy.toolbarBackText)
//                .setBackIcon(strategy.toolbarBackIcon)
//                .setBackTextColor(strategy.toolbarBackTextColor)
//                .setBackTextSize(strategy.toolbarBackTextSize)
//                .setBackTypeface(strategy.toolbarBackTypeface)
//            if (strategy.toolbarBackIconTintColor != null) {
//                mToolbar!!.setBackIconTintColor(strategy.toolbarBackIconTintColor)
//            }
//            // 设置标题栏Action控件属性
//            mToolbar!!.setDefaultActionTextColor(strategy.toolbarActionTextColor)
//                .setDefaultActionTextSize(strategy.toolbarActionTextSize)
//                .setDefaultActionTypeface(strategy.toolbarActionTypeface)
//            // 设置标题栏分割线属性
//            mToolbar!!.setDividerColor(strategy.toolbarDividerColor)
//                .setDividerHeight(strategy.toolbarDividerHeight)
//                .setDividerMargin(strategy.toolbarDividerMargin)
//                .setDividerVisible(strategy.isToolbarDividerVisible)
}