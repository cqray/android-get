package cn.cqray.android.state

import cn.cqray.android.app.GetDelegate
import cn.cqray.android.app.GetProvider

@JvmDefaultWithoutCompatibility
interface StateProvider : GetProvider {

    val stateDelegate: StateDelegate
        get() = GetDelegate.get(this)

    fun setBusy() = stateDelegate.setBusy()

    fun setBusy(text: String?) = stateDelegate.setBusy(text)

    fun setEmpty() = stateDelegate.setEmpty()

    fun setEmpty(text: String?) = stateDelegate.setEmpty(text)

    fun setError() = stateDelegate.setError()

    fun setError(text: String?) = stateDelegate.setError(text)

    fun setIdle() = stateDelegate.setIdle()

    fun setState(state: ViewState?, text: String?) = stateDelegate.setState(state, text)

}