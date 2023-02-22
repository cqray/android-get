package cn.cqray.android.state

/**
 * [StateDelegate]全局配置
 * @author Cqray
 */
class StateInit {

    /** 日志等级 **/
    var busyAdapter: StateAdapter<*>? = BusyAdapter()
        set(adapter) {
            field = adapter ?: BusyAdapter()
        }

    /** 日志等级 **/
    var emptyAdapter: StateAdapter<*>? = EmptyAdapter()
        set(adapter) {
            field = adapter ?: EmptyAdapter()
        }

    /** 日志等级 **/
    var errorAdapter: StateAdapter<*>? = ErrorAdapter()
        set(adapter) {
            field = adapter ?: ErrorAdapter()
        }

    var busyAdapterCreator: Function0<StateAdapter<*>> = { BusyAdapter() }

    var emptyAdapterCreator: Function0<StateAdapter<*>> = { EmptyAdapter() }

    var errorAdapterCreator: Function0<StateAdapter<*>> = { ErrorAdapter() }
}