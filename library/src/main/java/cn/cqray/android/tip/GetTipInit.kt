package cn.cqray.android.tip

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import cn.cqray.android.Get
import cn.cqray.android.init.BaseInit
import java.lang.reflect.Field

/**
 * Tip配置属性
 * @author Cqray
 **/
class GetTipInit : BaseInit() {

    /** 是否支持在Logcat中显示 **/
    var logcatEnable: Boolean? = null

    /** 重心 **/
    var gravity: Int? = null

    /** X值偏移量 **/
    var offsetX: Number? = null

    /** Y值偏移量 **/
    var offsetY: Number? = null

    /** 横向间隔值 **/
    var marginH: Number? = null

    /** 纵向间隔值 **/
    var marginV: Number? = null

    /** Tip最小宽度，单位DP **/
    var minWidth: Number? = null

    /** Tip最大宽度，单位DP **/
    var maxWidth: Number? = null

    /** 文本颜色 **/
    @ColorInt
    var textColor: Int? = null

    /** 文本样式 **/
    var textStyle: Int? = null

    /** 文本大小，单位SP **/
    var textSize: Number? = null

    /** 显示时长 **/
    @IntRange(from = 0, to = 3500)
    var duration: Int? = null

    /** 背景圆角 **/
    var backgroundRadius: Number? = null

    /** 背景颜色 **/
    var backgroundColor: Int? = null

    /** 合并默认值 **/
    fun mergeDefault() = also {
        // 默认配置
        val default = Get.init.tipInit.also { it.loadFromLocal() }
        // 获取所有属性
        val fields = mutableListOf<Field>().also {
            it.addAll(GetTipInit::class.java.fields)
            it.addAll(GetTipInit::class.java.declaredFields)
        }
        // 遍历所有属性，取出不为null的值并重新赋值
        fields.forEach { field ->
            runCatching {
                field.isAccessible = true
                val cur = field.get(this)
                // 空值，则赋予默认值
                if (cur == null) field.set(this, field.get(default))
            }
        }
    }
}