package cn.cqray.android.tip

import androidx.annotation.StringRes
import cn.cqray.android.util.ContextUtils

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetTipProvider {

    fun showTip(text: CharSequence?) = GetTip.show(text)

    fun showTip(text: CharSequence?, init: GetTipInit?) = GetTip.show(text, init)

    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>?
    ) = GetTip.show(text, hideCallback)

    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = GetTip.show(text, hideCallback, showCallback)

    fun showTip(
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?
    ) = GetTip.show(text, init, hideCallback)

    fun showTip(
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = GetTip.show(text, init, hideCallback, showCallback)

    fun showTip(@StringRes id: Int) = showTip(id, null, null, null)

    fun showTip(@StringRes id: Int, init: GetTipInit?) = showTip(id, init, null, null)

    fun showTip(
        @StringRes id: Int,
        hideCallback: Function0<Unit>?
    ) = showTip(id, null, hideCallback, null)

    fun showTip(
        @StringRes id: Int,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = showTip(id, null, hideCallback, showCallback)

    fun showTip(
        @StringRes id: Int,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?
    ) = showTip(id, init, hideCallback, null)

    fun showTip(
        @StringRes id: Int,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = GetTip.show(ContextUtils.getString(id), init, hideCallback, showCallback)
}