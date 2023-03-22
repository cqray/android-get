package cn.cqray.android.lifecycle

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import cn.cqray.android.Get

/**
 * [MutableLiveData]修改版
 * [setValue]和[postValue]功能完全一样，均可在子线程调用
 * @author Cqray
 */
@Suppress("unused")
class GetLiveData<T> : MutableLiveData<T> {
    constructor() : super()

    constructor(value: T) : super(value)

    override fun setValue(value: T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value)
        } else {
            Get.runOnUiThreadDelayed({ super.setValue(value) })
        }
    }

    override fun postValue(value: T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value)
        } else {
            Get.runOnUiThreadDelayed({ super.setValue(value) })
        }
    }
}