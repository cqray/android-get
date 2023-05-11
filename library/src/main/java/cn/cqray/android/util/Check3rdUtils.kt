package cn.cqray.android.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.HandlerDispatcher

/**
 * 检测第三方是否可以工具类
 * @author Cqray
 */
internal object Check3rdUtils {
    /** 是否支持Rx2 **/
    var isRxJava2Support = true

    /** 是否支持Rx3 **/
    var isRxJava3Support = true

    /** 是否支持协程 **/
    var isCoroutinesSupport = true

    fun check() {
        // 检测RxJava2
        runCatching {
            io.reactivex.schedulers.Schedulers.io()
            io.reactivex.android.schedulers.AndroidSchedulers.mainThread()
            isRxJava2Support = true
        }.onFailure { isRxJava2Support = false }
        // 检测RxJava3
        runCatching {
            io.reactivex.rxjava3.schedulers.Schedulers.io()
            io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread()
            isRxJava3Support = true
        }.onFailure { isRxJava3Support = false }
        // 检测协程
        runCatching {
            Dispatchers.IO
            HandlerDispatcher::class.java
            isCoroutinesSupport = true
        }.onFailure { isCoroutinesSupport = false }
    }
}