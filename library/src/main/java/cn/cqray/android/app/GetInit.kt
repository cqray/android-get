package cn.cqray.android.app

import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.anim.GetFragmentAnimator
import cn.cqray.android.log.GetLogInit
import cn.cqray.android.state.StateInit
import cn.cqray.android.tip.GetTipInit
import cn.cqray.android.ui.page.GetPaginationInit
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
    var stateInit: StateInit? = StateInit()
        set(init) {
            field = init ?: StateInit()
        }

    /** [Get]提示初始化配置  **/
    var tipInit: GetTipInit? = GetTipInit()
        set(init) {
            field = init ?: GetTipInit()
        }

    var toolbarInit: GetToolbarInit? = GetToolbarInit()
        set(init) {
            field = init ?: field
        }

    var paginationInit: GetPaginationInit? = GetPaginationInit()
        set(init) {
            field = init ?: field
        }

    /** [Get]Fragment全局动画 **/
    var fragmentAnimator: GetFragmentAnimator? = null
}