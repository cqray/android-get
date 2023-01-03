package cn.cqray.android.app2

import androidx.annotation.Keep
import cn.cqray.android.Get
import cn.cqray.android.log.LogInit
import cn.cqray.android.state.StateInit
import cn.cqray.android.tip.TipInit
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
    var tipInit: TipInit? = TipInit()
        set(init) {
            field = init ?: TipInit()
        }
}