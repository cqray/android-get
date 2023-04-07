package cn.cqray.android.tip

import androidx.annotation.StringRes
import cn.cqray.android.util.ContextUtils

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface TipProvider {

    fun showTip(text: CharSequence?) = Tip.show(text)

    fun showTip(text: CharSequence?, init: TipInit?) = Tip.show(text, init)

    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>?
    ) = Tip.show(text, hideCallback)

    fun showTip(
        text: CharSequence?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = Tip.show(text, hideCallback, showCallback)

    fun showTip(
        text: CharSequence?,
        init: TipInit?,
        hideCallback: Function0<Unit>?
    ) = Tip.show(text, init, hideCallback)

    fun showTip(
        text: CharSequence?,
        init: TipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = Tip.show(text, init, hideCallback, showCallback)

    fun showTip(@StringRes id: Int) = showTip(id, null, null, null)

    fun showTip(@StringRes id: Int, init: TipInit?) = showTip(id, init, null, null)

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
        init: TipInit?,
        hideCallback: Function0<Unit>?
    ) = showTip(id, init, hideCallback, null)

    fun showTip(
        @StringRes id: Int,
        init: TipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?
    ) = Tip.show(ContextUtils.getString(id), init, hideCallback, showCallback)
}