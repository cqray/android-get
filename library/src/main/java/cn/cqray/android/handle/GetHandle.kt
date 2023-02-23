package cn.cqray.android.handle

internal interface GetHandle {

    fun timer(tag: Any?, delayed: Int?, func: (() -> Unit)?)

    fun periodic(
        tag: Any?,
        delayed: Int?,
        duration: Int?,
        condition: ((Int) -> Boolean)?,
        func: (() -> Unit)?
    )

    fun cancel(tag: Any?)

    fun cancelAll()
}