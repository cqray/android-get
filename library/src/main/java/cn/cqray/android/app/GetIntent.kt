package cn.cqray.android.app

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import cn.cqray.android.anim.FragmentAnimator
import java.io.Serializable
import java.util.*

/**
 * 导航意图
 * @author Cqray
 */
@Suppress("unused")
class GetIntent {
    /** 目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity] **/
    var toClass: Class<*>? = null
        private set

    /** 回退目标界面[Class]，仅支持实现[GetNavProvider]的[Fragment]以及[Activity] **/
    var backToClass: Class<*>? = null
        private set

    /** 是否包含指定回退的界面  */
    var isPopToInclusive = false
        private set

    /** Fragment动画  */
    var fragmentAnimator: FragmentAnimator? = null
        private set

    /** 参数  */
    val arguments = Bundle()

    constructor()

    constructor(cls: Class<*>?) {
        if (isValidClass(cls)) toClass = cls
    }

    constructor(cls: Class<*>?, arguments: Bundle?) {
        if (isValidClass(cls)) toClass = cls
        this.arguments.putAll(arguments ?: Bundle())
    }

    /**
     * 跳转指定目标界面
     * @param to 指定界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     */
    fun setTo(to: Class<*>?) : GetIntent {
        if (isValidClass(to)) toClass = to
        return this
    }

    /**
     * 回退到指定目标界面
     * @param backTo 指定目标界面Class，仅支持实现[GetNavProvider]的[Fragment]以及[Activity]
     * @param inclusive 是否包含自身
     */
    fun setBackTo(backTo: Class<*>?, inclusive: Boolean): GetIntent {
        if (isValidClass(backTo)) backToClass = backTo
        isPopToInclusive = inclusive
        return this
    }

    fun setFragmentAnimator(animator: FragmentAnimator?): GetIntent {
        fragmentAnimator = animator
        return this
    }

    fun put(key: String?, value: Boolean?) = also { value?.let { arguments.putBoolean(key, it) } }

    fun put(key: String?, value: BooleanArray?) = also { arguments.putBooleanArray(key, value) }

    fun put(key: String?, value: Byte?) = also { value?.let { arguments.putByte(key, it) } }

    fun put(key: String?, value: ByteArray?) = also { arguments.putByteArray(key, value) }

    fun put(key: String?, value: Char?) = also { value?.let { arguments.putChar(key, it) } }

    fun put(key: String?, value: CharArray?) = also { arguments.putCharArray(key, value) }

    fun put(key: String?, value: Short?) = also { value?.let { arguments.putShort(key, it) } }

    fun put(key: String?, value: ShortArray?) = also { arguments.putShortArray(key, value) }

    fun put(key: String?, value: Int?) = also { value?.let { arguments.putInt(key, it) } }

    fun put(key: String?, value: IntArray?) = also { arguments.putIntArray(key, value) }

    fun put(key: String?, value: Float?) = also { value?.let { arguments.putFloat(key, it) } }

    fun put(key: String?, value: FloatArray?) = also { arguments.putFloatArray(key, value) }

    fun put(key: String?, value: Double?) = also { value?.let { arguments.putDouble(key, it) } }

    fun put(key: String?, value: DoubleArray?) = also { arguments.putDoubleArray(key, value) }

    fun put(key: String?, value: CharSequence?) = also { arguments.putCharSequence(key, value) }

    fun put(key: String?, value: Array<CharSequence>?) =
        also { arguments.putCharSequenceArray(key, value) }

    fun put(key: String?, value: String?) = also { arguments.putString(key, value) }

    fun put(key: String?, value: Array<String>?) = also { arguments.putStringArray(key, value) }

    fun put(key: String?, value: Serializable?) = also { arguments.putSerializable(key, value) }

    fun put(key: String?, value: Parcelable?) = also { arguments.putParcelable(key, value) }

    fun put(key: String?, value: Array<Parcelable>?) =
        also { arguments.putParcelableArray(key, value) }

    fun put(key: String?, value: ArrayList<*>?) = also { arguments.putSerializable(key, value) }

    fun put(bundle: Bundle) = also { arguments.putAll(bundle) }

    private fun isValidClass(cls: Class<*>?): Boolean {
        val isNull = cls == null
        val isActivity = !isNull && Activity::class.java.isAssignableFrom(cls!!)
        val isNavProvider = !isNull && GetNavProvider::class.java.isAssignableFrom(cls!!)
        if (isActivity || isNavProvider) return true
        //TODO
        return false
    }
}