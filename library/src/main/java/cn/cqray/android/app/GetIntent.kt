package cn.cqray.android.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import cn.cqray.android.anim.GetFragmentAnimator
import java.io.Serializable
import java.util.*

/**
 * 导航意图
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetIntent(
    /** 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]  **/
    val toClass: Class<*>
) {

    /** 参数  */
    val arguments = Bundle()

//    /** 回退目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity] **/
//    var backToClass: Class<*>? = null
//        private set

    /** 是否包含指定回退的界面  */
    var isBackInclusive: Boolean = false
        private set

    /** 是否是SingleTop模式，类似于 **/
    var isSingleTop: Boolean = true
        private set

    /** Fragment动画  */
    var fragmentAnimator: GetFragmentAnimator? = null
        private set

    /** 启动模式 **/
    @LaunchMode
    var launchMode: Int = STANDARD
        private set
//
//    /**
//     * 回退到指定目标界面（包含自身）
//     * @param backTo 指定目标界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
//     */
//    fun setBackTo(backTo: Class<*>) = setBackTo(backTo, true)
//
//    /**
//     * 回退到指定目标界面
//     * @param backTo 指定目标界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
//     * @param inclusive 是否包含自身
//     */
//    fun setBackTo(backTo: Class<*>?, inclusive: Boolean) = also {
//        backTo?.let { checkClass(backTo) }
//        backToClass = backTo
//        isBackInclusive = inclusive
//    }

    fun setLaunchMode(@LaunchMode mode: Int) = also { this.launchMode = mode }

    /**
     * 设置是否使用SingleTop模式，默认true
     * @param singleTop 是否使用SingleTop模式
     */
    fun setSingleTop(singleTop: Boolean) = also { isSingleTop = singleTop }

    fun setFragmentAnimator(animator: GetFragmentAnimator?) = also { fragmentAnimator = animator }

    fun put(key: String?, value: Boolean?) = put(key, value as Any?)

    fun put(key: String?, value: BooleanArray?) = also { arguments.putBooleanArray(key, value) }

    fun put(key: String?, value: Byte?) = put(key, value as Any?)

    fun put(key: String?, value: ByteArray?) = also { arguments.putByteArray(key, value) }

    fun put(key: String?, value: Char?) = put(key, value as Any?)

    fun put(key: String?, value: CharArray?) = also { arguments.putCharArray(key, value) }

    fun put(key: String?, value: Short?) = put(key, value as Any?)

    fun put(key: String?, value: ShortArray?) = also { arguments.putShortArray(key, value) }

    fun put(key: String?, value: Int?) = put(key, value as Any?)

    fun put(key: String?, value: IntArray?) = also { arguments.putIntArray(key, value) }

    fun put(key: String?, value: Float?) = put(key, value as Any?)

    fun put(key: String?, value: FloatArray?) = also { arguments.putFloatArray(key, value) }

    fun put(key: String?, value: Double?) = put(key, value as Any?)

    fun put(key: String?, value: DoubleArray?) = also { arguments.putDoubleArray(key, value) }

    fun put(key: String?, value: CharSequence?) = also { arguments.putCharSequence(key, value) }

    fun put(key: String?, value: Array<CharSequence>?) = also { arguments.putCharSequenceArray(key, value) }

    fun put(key: String?, value: String?) = also { arguments.putString(key, value) }

    fun put(key: String?, value: Array<String>?) = also { arguments.putStringArray(key, value) }

    fun put(key: String?, value: Serializable?) = also { arguments.putSerializable(key, value) }

    fun put(key: String?, value: Parcelable?) = also { arguments.putParcelable(key, value) }

    fun put(key: String?, value: Array<Parcelable>?) = also { arguments.putParcelableArray(key, value) }

    fun put(key: String?, value: ArrayList<*>?) = also { arguments.putSerializable(key, value) }

    fun put(bundle: Bundle) = also { arguments.putAll(bundle) }

    fun remove(key: String?) = also { arguments.remove(key) }

    fun clear() = also { arguments.clear() }

    /**
     * 处理存放基础类型数据为null的情况
     * @param key 键
     * @param value 值
     */
    private fun put(key: String?, value: Any?) = also {
        when (value) {
            null -> arguments.remove(key)
            is Boolean -> arguments.putBoolean(key, value)
            is Byte -> arguments.putByte(key, value)
            is Char -> arguments.putChar(key, value)
            is Short -> arguments.putShort(key, value)
            is Int -> arguments.putInt(key, value)
            is Float -> arguments.putFloat(key, value)
            is Double -> arguments.putDouble(key, value)
        }
    }

    /**
     * 检查Class是否符合预期
     * @param clazz 待检查类
     */
    private fun checkClass(clazz: Class<*>) {
        val isActivity = Activity::class.java.isAssignableFrom(clazz)
        val isFragment = Fragment::class.java.isAssignableFrom(clazz)
        val isProvider = GetNavProvider::class.java.isAssignableFrom(clazz)
        if (!isProvider) throw RuntimeException("[${clazz.simpleName}] must implements GetNavProvider.")
        if (!isActivity && !isFragment) throw RuntimeException("[${clazz.simpleName}] must be Activity or Fragment.")
    }

    companion object {
        const val STANDARD = Intent.FLAG_ACTIVITY_NEW_TASK
        const val SINGLE_TOP = Intent.FLAG_ACTIVITY_SINGLE_TOP
        const val SINGLE_TASK = Intent.FLAG_ACTIVITY_CLEAR_TOP

        @IntDef
        @Retention(AnnotationRetention.SOURCE)
        annotation class LaunchMode
    }
}