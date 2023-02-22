package cn.cqray.android.state

/**
 * 状态提供器
 * @author Cqray
 */
@Suppress("Deprecation")
@JvmDefaultWithoutCompatibility
interface StateProvider {

    /**
     * 获取并初始化[StateDelegate]
     */
    val stateDelegate: StateDelegate

    /**
     * 设置忙碌状态
     */
    @JvmDefault
    fun setBusy() = stateDelegate.setBusy(null)

    /**
     * 设置忙碌状态
     * @param text 文本信息
     */
    @JvmDefault
    fun setBusy(text: String?) = stateDelegate.setBusy(text)

    /**
     * 设置空状态
     */
    @JvmDefault
    fun setEmpty() = stateDelegate.setEmpty(null)

    /**
     * 设置空状态
     * @param text 文本信息
     */
    @JvmDefault
    fun setEmpty(text: String?) = stateDelegate.setEmpty(text)

    /**
     * 设置异常状态
     */
    @JvmDefault
    fun setError() = stateDelegate.setError(null)

    /**
     * 设置异常状态
     * @param text 文本信息
     */
    @JvmDefault
    fun setError(text: String?) = stateDelegate.setError(text)

    /**
     * 设置空闲状态
     */
    @JvmDefault
    fun setIdle() = stateDelegate.setIdle()

    /**
     * 设置忙碌状态
     * @param state 视图状态
     * @param text 文本信息
     */
    @JvmDefault
    fun setState(state: ViewState, text: String?) = stateDelegate.setState(state, text)

}