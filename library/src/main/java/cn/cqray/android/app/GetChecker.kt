package cn.cqray.android.app

class GetChecker {

    private var lastClickTime = 0L

    private val lastClickDuration = 150

    fun isFastClick(): Boolean {
        val cur = System.currentTimeMillis()
        if (cur - lastClickTime < lastClickDuration) {
            return true
        }
        lastClickTime = cur
        return false
    }
}