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

    fun put(key: String, value: Boolean): GetIntent {
        arguments.putBoolean(key, value)
        return this
    }

    fun put(key: String, value: BooleanArray): GetIntent {
        arguments.putBooleanArray(key, value)
        return this
    }

    fun put(key: String, value: Byte): GetIntent {
        arguments.putByte(key, value)
        return this
    }

    fun put(key: String, value: ByteArray): GetIntent {
        arguments.putByteArray(key, value)
        return this
    }

    fun put(key: String, value: Char): GetIntent {
        arguments.putChar(key, value)
        return this
    }

    fun put(key: String, value: CharArray): GetIntent {
        arguments.putCharArray(key, value)
        return this
    }

    fun put(key: String, value: Short): GetIntent {
        arguments.putShort(key, value)
        return this
    }

    fun put(key: String, value: ShortArray): GetIntent {
        arguments.putShortArray(key, value)
        return this
    }

    fun put(key: String, value: Int): GetIntent {
        arguments.putInt(key, value)
        return this
    }

    fun put(key: String, value: IntArray): GetIntent {
        arguments.putIntArray(key, value)
        return this
    }

    fun put(key: String, value: Float): GetIntent {
        arguments.putFloat(key, value)
        return this
    }

    fun put(key: String, value: FloatArray): GetIntent {
        arguments.putFloatArray(key, value)
        return this
    }

    fun put(key: String, value: Double): GetIntent {
        arguments.putDouble(key, value)
        return this
    }

    fun put(key: String, value: DoubleArray): GetIntent {
        arguments.putDoubleArray(key, value)
        return this
    }

    fun put(key: String, value: CharSequence): GetIntent {
        arguments.putCharSequence(key, value)
        return this
    }

    fun put(key: String, value: Array<CharSequence?>): GetIntent {
        arguments.putCharSequenceArray(key, value)
        return this
    }

    fun put(key: String, value: String): GetIntent {
        arguments.putString(key, value)
        return this
    }

    fun put(key: String, value: Array<String?>): GetIntent {
        arguments.putStringArray(key, value)
        return this
    }

    fun put(key: String, value: Serializable): GetIntent {
        arguments.putSerializable(key, value)
        return this
    }

    fun put(key: String, value: Parcelable): GetIntent {
        arguments.putParcelable(key, value)
        return this
    }

    fun put(key: String, value: Array<Parcelable?>): GetIntent {
        arguments.putParcelableArray(key, value)
        return this
    }

    fun put(key: String, value: ArrayList<*>): GetIntent {
        arguments.putSerializable(key, value)
        return this
    }

    fun put(bundle: Bundle): GetIntent {
        arguments.putAll(bundle)
        return this
    }

    private fun isValidClass(cls: Class<*>?): Boolean {
        val isNull = cls == null
        val isActivity = !isNull && Activity::class.java.isAssignableFrom(cls!!)
        val isNavProvider = !isNull && GetNavProvider::class.java.isAssignableFrom(cls!!)
        if (isActivity || isNavProvider) return true
        //TODO
        return false
    }
}