package cn.cqray.android.util

import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * ButterKnife工具类
 * @author Cqray
 */
object ButterKnifeUtils {

    /** ButterKnife是否可用  */
    private var sSupported = true

    /**
     * ButterKnife绑定控件
     * @param target 目标
     * @param source 来源
     * @return 绑定实例
     */
    @JvmStatic
    fun bind(target: Any?, source: View): Any? {
        if (target == null || !sSupported) return null
        val unBinder: Unbinder?
        try {
            unBinder = ButterKnife.bind(target, source)
            sSupported = true
        } catch (t: NoClassDefFoundError) {
            sSupported = false
            return null
        }
        return unBinder
    }

    /**
     * 解除ButterKnife绑定
     * @param unBinder 绑定实例[Unbinder]
     */
    @JvmStatic
    fun unbind(unBinder: Any?) {
        if (unBinder == null || !sSupported) return
        if (unBinder is Unbinder) unBinder.unbind()
        sSupported = try {
            if (unBinder is Unbinder) unBinder.unbind()
            true
        } catch (e: NoClassDefFoundError) {
            false
        }
    }
}