package cn.cqray.android.state

class GetStateInit : java.io.Serializable {
    /** 忙碌适配器获取器 **/
    var busyGet: Function0<GetStateAdapter<*>> = { GetBusyAdapter() }

    /** 空白适配器获取器 **/
    var emptyGet: Function0<GetStateAdapter<*>> = { GetEmptyAdapter() }

    /** 异常适配器获取器 **/
    var errorGet: Function0<GetStateAdapter<*>> = { GetErrorAdapter() }
}