package cn.cqray.android.handle

import androidx.lifecycle.LifecycleOwner

class GetHandleImpl1(private val lifecycleOwner: LifecycleOwner) : GetHandle {

    override fun timer(tag: Any?, delayed: Int?, func: (() -> Unit)?) {
        TODO("Not yet implemented")
    }

    override fun periodic(
        tag: Any?,
        delayed: Int?,
        duration: Int?,
        condition: ((Int) -> Boolean)?,
        func: (() -> Unit)?
    ) {
        TODO("Not yet implemented")
    }

    override fun cancel(tag: Any?) {
        TODO("Not yet implemented")
    }

    override fun cancelAll() {
        TODO("Not yet implemented")
    }
}