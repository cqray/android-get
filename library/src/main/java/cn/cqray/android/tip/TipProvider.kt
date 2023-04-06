package cn.cqray.android.tip

import androidx.annotation.StringRes
import cn.cqray.android.util.ContextUtils

/**
 * Tip提供者
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface TipProvider {

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     */
    fun showTip(text: CharSequence?) = Tip.show(text, null)

    /**
     * 显示Tip
     * @param id 文本资源ID
     */
    fun showTip(@StringRes id: Int) = Tip.show(ContextUtils.getString(id), null)

    /**
     * 显示Tip
     * @param text 文本内容 [CharSequence]
     * @param init 配置参数 [TipInit]
     */
    fun showTip(text: CharSequence?, init: TipInit?) = Tip.show(text, init)

    /**
     * 显示Tip
     * @param id 文本资源ID
     * @param init 配置参数 [TipInit]
     */
    fun showTip(@StringRes id: Int, init: TipInit?) = Tip.show(ContextUtils.getString(id), init)
}