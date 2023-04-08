package cn.cqray.android.state

/**
 * [GetStateDelegate]全局配置
 * @author Cqray
 */
class GetStateInit {

    /** 忙碌适配器 **/
    var busyAdapterCreator: Function0<GetStateAdapter<*>> = { GetBusyAdapter() }

    /** 空白适配器 **/
    var emptyAdapterCreator: Function0<GetStateAdapter<*>> = { GetEmptyAdapter() }

    /** 异常适配器 **/
    var errorAdapterCreator: Function0<GetStateAdapter<*>> = { GetErrorAdapter() }
}