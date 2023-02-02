package cn.cqray.android.helper

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class GetClickHelper {

//    private var lastClickTime = 0L

    private val lastClickTime = AtomicLong(0)

    private val lastClickDuration = 150L

//    fun isFastClick(): Boolean {
//        val cur = System.currentTimeMillis()
//        if (cur - lastClickTime < lastClickDuration) {
//            return true
//        }
//        lastClickTime = cur
//        return false
//    }

    /** 是否是快速点击 **/
    fun isQuickClick(): Boolean {
        val last = lastClickTime.get()
        val current = System.currentTimeMillis()
        return (current - last < lastClickDuration).also {
            // 不是快速点击则缓存上次点击时间
            if (!it) lastClickTime.set(current)
        }
    }
}