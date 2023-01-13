package cn.cqray.android.app

import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.anim.FragmentAnimator
import cn.cqray.android.log.LogInit
import cn.cqray.android.state.StateInit
import cn.cqray.android.tip.GetTipInit
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

    /** [Get]Fragment全局动画 **/
    var fragmentAnimator: FragmentAnimator? = null
}