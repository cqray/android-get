package cn.cqray.android.ui.multi

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import java.io.Serializable
import java.util.ArrayList

/**
 * Tab项
 * @author Cqray
 */
@Suppress("unused")
class MultiItem2(
    /** 目标Fragment的[Class] **/
    @Suppress val targetClass: Class<out Fragment>,
    /** 目标Fragment名称 **/
    val name: String?
) {

    /** 对应图标 **/
    @Suppress
    var icon: Int? = null
        private set

    /** 传入参数  */
    @Suppress
    val arguments = Bundle()

    fun icon(@DrawableRes icon: Int?) = also { this.icon = icon }

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
}