package cn.cqray.android.lifecycle

import android.os.Looper
import androidx.lifecycle.MutableLiveData

class GetLiveData<T> : MutableLiveData<T> {
    constructor() : super() {}
    constructor(value: T) : super(value) {}

    override fun setValue(value: T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value)
        } else {
            super.postValue(value)
        }
    }

    override fun postValue(value: T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value)
        } else {
            super.postValue(value)
        }
    }
}