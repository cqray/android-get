package cn.cqray.android.util

import android.app.Activity
import android.app.Dialog
import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * ButterKnife工具类
 * @author Cqray
 */
object ButterKnifeUtils {
    /** ButterKnife是否可用  */
    private var mButterKnifeSupport = true

//    /**
//     * ButterKnife绑定Activity实例
//     * @param target Activity实例
//     * @return 绑定实例
//     */
//    fun bind(target: Activity): Any? {
//        return if (mButterKnifeSupport) {
//            try {
//                ButterKnife.bind(target)
//            } catch (t: Throwable) {
//                setClassNotFound(t)
//                null
//            }
//        } else null
//    }
//
//    /**
//     * ButterKnife绑定控件
//     * @param target 控件
//     * @return 绑定实例
//     */
//    fun bind(target: View): Any? {
//        return if (mButterKnifeSupport) {
//            try {
//                ButterKnife.bind(target)
//            } catch (t: Throwable) {
//                setClassNotFound(t)
//                null
//            }
//        } else null
//    }
//
//    /**
//     * ButterKnife绑定对话框
//     * @param target 控件
//     * @return 绑定实例
//     */
//    fun bind(target: Dialog): Any? {
//        return if (mButterKnifeSupport) {
//            try {
//                ButterKnife.bind(target)
//            } catch (t: Throwable) {
//                setClassNotFound(t)
//                null
//            }
//        } else null
//    }

    /**
     * ButterKnife绑定控件
     * @param target 目标
     * @param source 来源
     * @return 绑定实例
     */
    @JvmStatic
    fun bind(target: Any?, source: View): Any? {
        if (target == null || !mButterKnifeSupport) return null
        return try {
            ButterKnife.bind(target, source)
        } catch (t: Throwable) {
            setClassNotFound(t)
            null
        }
    }

    /**
     * 解除ButterKnife绑定
     * @param unBinder 绑定实例[Unbinder]
     */
    @JvmStatic
    fun unbind(unBinder: Any?) {
        if (unBinder == null || !mButterKnifeSupport) return
        try {
            if (unBinder is Unbinder) unBinder.unbind()
        } catch (t: Throwable) {
            setClassNotFound(t)
        }
    }

    /**
     * 设置[ButterKnife]没有找到
     * @param th 异常[Throwable]
     */
    private fun setClassNotFound(th: Throwable) {
        if (th is NoClassDefFoundError) {
            mButterKnifeSupport = false
        }
    }
}