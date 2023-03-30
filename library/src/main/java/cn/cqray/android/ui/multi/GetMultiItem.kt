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
@Suppress("Unused")
class GetMultiItem @JvmOverloads constructor(
    /** 目标Fragment的[Class] **/
    val targetClass: Class<out Fragment>,
    /** 目标Fragment名称 **/
    val name: String? = null
) {

    /** 选中时的图标 **/
    var selectIcon: Int? = null
        private set

    /** 未选中时的图标 **/
    var unselectIcon: Int? = null
        private set

    /** 传入参数  */
    val arguments = Bundle()

    fun selectIcon(@DrawableRes icon: Int) = also { selectIcon = icon }

    fun unselectIcon(@DrawableRes icon: Int) = also { unselectIcon = icon }

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
}