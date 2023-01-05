package cn.cqray.android.state

@JvmDefaultWithoutCompatibility
interface StateProvider {

    val stateDelegate: StateDelegate
        get() = StateDelegate.get(this)

    fun setBusy() = stateDelegate.setBusy(null)

    fun setBusy(text: String?) = stateDelegate.setBusy(text)

    fun setEmpty() = stateDelegate.setEmpty(null)

    fun setEmpty(text: String?) = stateDelegate.setEmpty(text)

    fun setError() = stateDelegate.setError(null)

    fun setError(text: String?) = stateDelegate.setError(text)

    fun setIdle() = stateDelegate.setIdle()

    fun setState(state: ViewState?, text: String?) = stateDelegate.setState(state, text)

}