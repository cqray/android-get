package cn.cqray.android.state

/**
 * 状态提供器
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface StateProvider {

    /**
     * 获取并初始化[StateDelegate]
     */
    val stateDelegate: StateDelegate

    /**
     * 设置忙碌状态
     */
    fun setBusy() = stateDelegate.setBusy()

    /**
     * 设置忙碌状态
     * @param text 文本信息
     */
    fun setBusy(text: String?) = stateDelegate.setBusy(text)

    /**
     * 设置空状态
     */
    fun setEmpty() = stateDelegate.setEmpty()

    /**
     * 设置空状态
     * @param text 文本信息
     */
    fun setEmpty(text: String?) = stateDelegate.setEmpty(text)

    /**
     * 设置异常状态
     */
    fun setError() = stateDelegate.setError()

    /**
     * 设置异常状态
     * @param text 文本信息
     */
    fun setError(text: String?) = stateDelegate.setError(text)

    /**
     * 设置空闲状态
     */
    fun setIdle() = stateDelegate.setIdle()

    /**
     * 设置界面状态
     * @param state 视图状态
     */
    fun setState(state: ViewState) = stateDelegate.setState(state)

    /**
     * 设置界面状态
     * @param state 视图状态
     * @param text 文本信息
     */
    fun setState(state: ViewState, text: String) = stateDelegate.setState(state, text)
}