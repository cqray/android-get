package cn.cqray.android.handle

interface GetHandleCondition<T> {

    fun isTerminate(data: T): Boolean

}