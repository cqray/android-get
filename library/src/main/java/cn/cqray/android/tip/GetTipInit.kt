package cn.cqray.android.tip

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import cn.cqray.android.Get
import cn.cqray.android.app.GetBaseInit
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 * Tip配置属性
 * @author Cqray
 **/
class GetTipInit : GetBaseInit() {

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
    var duration: Int? = null

    /** 背景圆角 **/
    var backgroundRadius: Number? = null

    /** 背景颜色 **/
    var backgroundColor: Int? = null

    /** 合并默认值 **/
    fun mergeDefault() = also {
        // 默认配置
        val default = Get.init.tipInit.also { it.loadFromLocal() }
        // 遍历属性
        javaClass.kotlin.declaredMemberProperties.forEach {
            runCatching {
                if (it.javaClass.getAnnotation(Transient::class.java) != null) {
                    // 不执行的类型
                    return@runCatching
                }
                val type = it.returnType
                it.javaField?.let {
                    it.isAccessible = true
                    val cur = it.get(this)
                    val def = it.get(default)
                    // 空值，则赋予默认值
                    if (cur == null) {
                        // 值不为NULL，直接赋值
                        if (def != null) it.set(this, def)
                        // 为NULL则需要判定是否可以为NULL才赋值
                        else if (type.isMarkedNullable) it.set(this, null)
                    }
                }
            }
        }
    }
}