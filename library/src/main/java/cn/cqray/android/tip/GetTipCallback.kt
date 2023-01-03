package cn.cqray.android.tip

/**
 * Tip 回调
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetTipCallback {
    /**
     * Tip 显示
     */
    fun onShow() {}

    /**
     * Tip 隐藏
     */
    fun onHide()
}