package cn.cqray.android.state

/**
 * 状态提供器
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetStateProvider {

    /**
     * 获取并初始化[GetStateDelegate]
     */
    val stateDelegate: GetStateDelegate

    /**
     * 设置忙碌状态
     */
    fun setBusy() = stateDelegate.setBusy(null)

    /**
     * 设置忙碌状态
     * @param text 文本信息
     */
    fun setBusy(text: String?) = stateDelegate.setBusy(text)

    /**
     * 设置空状态
     */
    fun setEmpty() = stateDelegate.setEmpty(null)

    /**
     * 设置空状态
     * @param text 文本信息
     */
    fun setEmpty(text: String?) = stateDelegate.setEmpty(text)

    /**
     * 设置异常状态
     */
    fun setError() = stateDelegate.setError(null)

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
     * 设置忙碌状态
     * @param state 视图状态
     * @param text 文本信息
     */
    fun setState(state: GetViewState, text: String?) = stateDelegate.setState(state, text)
}