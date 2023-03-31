package cn.cqray.android.state

/**
 * [StateDelegate]全局配置
 * @author Cqray
 */
class StateInit {

    /** 忙碌适配器 **/
    var busyAdapterCreator: Function0<StateAdapter<*>> = { BusyAdapter() }

    /** 空白适配器 **/
    var emptyAdapterCreator: Function0<StateAdapter<*>> = { EmptyAdapter() }

    /** 异常适配器 **/
    var errorAdapterCreator: Function0<StateAdapter<*>> = { ErrorAdapter() }
}