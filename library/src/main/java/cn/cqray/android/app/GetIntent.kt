package cn.cqray.android.app

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import cn.cqray.android.anim.GetFragmentAnimator
import java.io.Serializable
import java.util.*

/**
 * 导航意图
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class GetIntent : Serializable {

    constructor()

    constructor(toClass: Class<*>) {
        checkClass(toClass)
        intentCache[0] = toClass
    }

    constructor(toClass: Class<*>, args: Bundle) {
        checkClass(toClass)
        intentCache[0] = toClass
        arguments.putAll(args)
    }

    /** 缓存，无实际意义，只是为了字段为final，好看 **/
    private val intentCache = arrayOfNulls<Any>(4)

    /** 参数  */
    val arguments = Bundle()

    /** 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity] **/
    val toClass get() = intentCache[0] as Class<*>?

    /** 回退目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity] **/
    val backToClass get() = intentCache[1] as Class<*>?

    /** 是否包含指定回退的界面  */
    val backInclusive get() = (intentCache[2] as Boolean?) ?: false

    /** Fragment动画  */
    val fragmentAnimator get() = intentCache[3] as GetFragmentAnimator?

    /**
     * 跳转指定目标界面
     * @param to 指定界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     */
    fun setTo(to: Class<*>) = also {
        checkClass(to)
        intentCache[0] = to
    }

    /**
     * 回退到指定目标界面（包含自身）
     * @param backTo 指定目标界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     */
    fun setBackTo(backTo: Class<*>) = setBackTo(backTo, true)

    /**
     * 回退到指定目标界面
     * @param backTo 指定目标界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含自身
     */
    fun setBackTo(backTo: Class<*>?, inclusive: Boolean) = also {
        backTo?.let { checkClass(backTo) }
        intentCache[1] = backTo
        intentCache[2] = inclusive
    }

    fun setFragmentAnimator(animator: GetFragmentAnimator?) = also { intentCache[3] = animator }

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
}