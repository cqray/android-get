package cn.cqray.android.app

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.anim.VerticalAnimator
import cn.cqray.android.log.GetLogInit
import cn.cqray.android.state.GetStateInit
import cn.cqray.android.tip.GetTipInit
import cn.cqray.android.ui.page.GetPaginationInit
import cn.cqray.android.util.Colors
import cn.cqray.android.widget.GetToolbarInit
import java.io.Serializable

@Keep
class GetInit : Serializable {

    /** [Get]日志打印初始化配置 **/
    var logInit: GetLogInit? = GetLogInit()
        set(init) {
            field = init ?: GetLogInit()
        }

    /** [Get]提示初始化配置  **/
    var stateInit: GetStateInit? = GetStateInit()
        set(init) {
            field = init ?: GetStateInit()
        }

    /** [Get]提示初始化配置  **/
    var tipInit: GetTipInit = GetTipInit()

    var toolbarInit: GetToolbarInit? = GetToolbarInit()
        set(init) {
            field = init ?: field
        }

    var paginationInit: GetPaginationInit? = GetPaginationInit()
        set(init) {
            field = init ?: field
        }

    /** [Get]Fragment全局动画 **/
    var fragmentAnimator: FragmentAnimator = VerticalAnimator()

    var fragmentBackgroundGet: Function0<Drawable?> = { ColorDrawable(Colors.background()) }
}