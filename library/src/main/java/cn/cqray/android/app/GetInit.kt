package cn.cqray.android.app

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.anim.VerticalAnimator
import cn.cqray.android.graphics.GetDrawable
import cn.cqray.android.log.GetLogInit
import cn.cqray.android.state.*
import cn.cqray.android.tip.GetTipInit
import cn.cqray.android.ui.page.GetPaginationInit
import cn.cqray.android.toolbar.GetToolbarInit

@Keep
@Suppress("Unused")
class GetInit : GetBaseInit() {

    /** [Get]日志打印初始化配置 **/
    var logInit = GetLogInit()

    /** [Get]提示初始化配置  **/
    var tipInit = GetTipInit()

    /** 标题初始化 **/
    var toolbarInit = GetToolbarInit()

    /** 分页初始化 **/
    var paginationInit = GetPaginationInit()

    /** 状态初始化 **/
    var stateInit = GetStateInit()

    /** [Get]Fragment全局动画 **/
    var fragmentAnimator: FragmentAnimator = VerticalAnimator()

    /** 背景 **/
    private val _fragmentBackground = GetDrawable().also { it.set(R.color.background) }

    /** Fragment背景颜色 **/
    val fragmentBackground: Drawable? get() = _fragmentBackground.get()

    fun setFragmentBackground(drawable: Drawable?) = _fragmentBackground.set(drawable)

    fun setFragmentBackground(bitmap: Bitmap?) = _fragmentBackground.set(bitmap)

    @JvmOverloads
    fun setFragmentBackground(any: Int, forceColor: Boolean = false) = _fragmentBackground.set(any, forceColor)
}