package cn.cqray.android.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.HandlerDispatcher

/**
 * 检测第三方是否可以工具类
 * @author Cqray
 */
internal object Check3rdUtils {

    /// 第三方
    private enum class Sdk {
        RxJava2,
        RxJava3,
        Coroutines,
    }

    /// 检查结果
    private val checks = mutableMapOf<Sdk, Boolean>()

    init {
        checks[Sdk.RxJava2] = true
        checks[Sdk.RxJava3] = true
        checks[Sdk.Coroutines] = true
    }

    /** 是否支持Rx2 **/
    val isRxJava2Support get() = checks[Sdk.RxJava2]!!

    /** 是否支持Rx3 **/
    val isRxJava3Support get() = checks[Sdk.RxJava3]!!

    /** 是否支持协程 **/
    val isCoroutinesSupport get() = checks[Sdk.Coroutines]!!

    /**
     * 检查第三方SDK是否支持
     */
    fun check() {
        // 检测协程
        runCatching {
            Dispatchers.IO
            HandlerDispatcher::class.java
            checks[Sdk.Coroutines] = true
        }.onFailure { checks[Sdk.Coroutines] = false }
        // 检测RxJava2
        runCatching {
            io.reactivex.schedulers.Schedulers.io()
            io.reactivex.android.schedulers.AndroidSchedulers.mainThread()
            checks[Sdk.RxJava2] = true
        }.onFailure { checks[Sdk.RxJava2] = false }
        // 检测RxJava3
        runCatching {
            io.reactivex.rxjava3.schedulers.Schedulers.io()
            io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread()
            checks[Sdk.RxJava3] = true
        }.onFailure { checks[Sdk.RxJava3] = false }
    }

}