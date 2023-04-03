package cn.cqray.android.tip

import android.util.Log
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.config.IToastInterceptor
import java.lang.reflect.Modifier

/**
 * [Tip]日志拦截器
 * @author Cqray
 */
internal class TipLogInterceptor : IToastInterceptor {

    override fun intercept(params: ToastParams): Boolean {
        printToast(params.text)
        return false
    }

    private fun printToast(text: CharSequence) {
        // 是否打印日志
        if (!isLogEnable) return
        // 获取调用的堆栈信息
        val stackTraces = Throwable().stackTrace
        for (stackTrace in stackTraces) {
            // 获取代码行数
            val lineNumber = stackTrace.lineNumber
            if (lineNumber <= 0) continue
            // 获取类的全路径
            val className = stackTrace.className
            runCatching {
                if (isPrintClass(Class.forName(className))) {
                    // 这里解释一下，为什么不用 Log.d，而用 Log.i，因为 Log.d 在魅族 16th 手机上面无法输出日志
                    Log.i("Tip", "(" + stackTrace.fileName + ":" + lineNumber + ") " + text.toString())
                    // 跳出循环
                    return
                }
            }
        }
    }

    val isLogEnable: Boolean = true

    /**
     * 是否是打印日志的类
     * @param clazz 指定类
     */
    private fun isPrintClass(clazz: Class<*>): Boolean {
        // 排查以下几种情况：
        // 1. 排除自身
        // 2. 排除 Toaster、TipDefaultAdapter、TipDelegate 类
        // 3. 排除接口类
        // 4. 排除抽象类
        val needFilter = (this.javaClass == clazz) or
                (Toaster::class.java == clazz) or
//                (TipDefaultAdapter::class.java == clazz) or
//                (GetTipDelegate::class.java == clazz) or
                (clazz.isInterface) or
                (Modifier.isAbstract(clazz.modifiers))
        // 不需要过滤，则说明是打印的类
        return !needFilter
    }
}