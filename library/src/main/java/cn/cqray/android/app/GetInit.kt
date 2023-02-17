package cn.cqray.android.app

import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.log.LogInit
import cn.cqray.android.state.StateInit
import cn.cqray.android.tip.GetTipInit
import cn.cqray.android.ui.page.PaginationInit
import cn.cqray.android.widget.ToolbarInit
import java.io.Serializable

@Keep
class GetInit : Serializable {

    /** [Get]日志打印初始化配置 **/
    var logInit: LogInit? = LogInit()
        set(init) {
            field = init ?: LogInit()
        }

    /** [Get]提示初始化配置  **/
    var stateInit: StateInit? = StateInit()
        set(init) {
            field = init ?: StateInit()
        }

    /** [Get]提示初始化配置  **/
    var tipInit: GetTipInit? = GetTipInit()
        set(init) {
            field = init ?: GetTipInit()
        }

    var toolbarInit: ToolbarInit? = ToolbarInit()
        set(init) {
            field = init ?: field
        }

    var paginationInit: PaginationInit? = PaginationInit()
        set(init) {
            field = init ?: field
        }

    /** [Get]Fragment全局动画 **/
    var fragmentAnimator: FragmentAnimator? = null
}