package cn.cqray.android.util

import android.util.TypedValue

/**
 * 尺寸单位
 * @author Cqray
 */
enum class SizeUnit(val type: Int) {

    PX(TypedValue.COMPLEX_UNIT_PX),
    DIP(TypedValue.COMPLEX_UNIT_DIP),
    SP(TypedValue.COMPLEX_UNIT_SP),
    PT(TypedValue.COMPLEX_UNIT_PT),
    IN(TypedValue.COMPLEX_UNIT_IN),
    MM(TypedValue.COMPLEX_UNIT_MM);

}