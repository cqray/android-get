package cn.cqray.android.handle

/**
 * [GetHandleDelegate]提供器
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetHandleProvider {

    /**
     *  获取并初始化[GetHandleProvider]
     */
    val handleDelegate: GetHandleDelegate get() = GetHandleDelegate.get(this)

    /**
     * 执行一次定时任务
     * @param func      回调
     * @param delayed   延时时间
     */
    fun timer(func: Function?, delayed: Int?) = handleDelegate.timer(null, func, delayed)

    /**
     * 执行一次定时任务
     * @param tag       标识
     * @param func      回调
     * @param delayed   延时时间
     */
    fun timer(tag: Any?, func: Function?, delayed: Int?) =
        handleDelegate.timer(tag, func, delayed)

    /**
     * 无限次周期性地执行任务
     * @param func      回调
     * @param duration  任务间隔时间
     */
    fun periodic(
        func: Function?,
        duration: Int?
    ) = handleDelegate.periodic(null, func, duration, duration, null)

    /**
     * 无限次周期性地执行任务
     * @param func      回调
     * @param duration  任务间隔时间
     */
    fun periodic(
        func: (() -> Unit)?,
        duration: Int?
    ) = handleDelegate.periodic(null, func, duration, duration, null)

    /**
     * 无限次周期性地执行任务
     * @param tag       指定标识
     * @param func      回调
     * @param duration  任务间隔时间
     */
    fun periodic(
        tag: Any?,
        func: Function?,
        duration: Int?
    ) = handleDelegate.periodic(tag, func, duration, duration, null)

    /**
     * 无限次周期性地执行任务
     * @param tag       指定标识
     * @param func      回调
     * @param delayed   初始延迟时间
     * @param duration  任务间隔时间
     */
    fun periodic(
        tag: Any?,
        func: Function?,
        delayed: Int?,
        duration: Int?
    ) = handleDelegate.periodic(tag, func, delayed, duration, null)

    /**
     * 周期性地执行任务
     * @param func      回调
     * @param duration  任务间隔时间
     * @param doCount   执行次数，null及小于等于0为无数次
     */
    fun periodic(
        func: Function?,
        duration: Int?,
        condition: ((Int) -> Boolean)?
    ) = handleDelegate.periodic(null, func, duration, duration, condition)

    /**
     * 周期性地执行任务
     * @param func      回调
     * @param delayed   初始延迟时间
     * @param duration  任务间隔时间
     * @param doCount   执行次数，null及小于等于0为无数次
     */
    fun periodic(
        func: Function?,
        delayed: Int?,
        duration: Int?,
        condition: ((Int) -> Boolean)?
    ) = handleDelegate.periodic(null, func, delayed, duration, condition)

    /**
     * 周期性地执行任务
     * @param tag       指定标识
     * @param func      回调
     * @param delayed   初始延迟时间
     * @param duration  任务间隔时间
     * @param doCount   执行次数，null及小于等于0为无数次
     */
    fun periodic(
        tag: Any?,
        func: Function?,
        delayed: Int?,
        duration: Int?,
        condition: ((Int) -> Boolean)?
    ) = handleDelegate.periodic(tag, func, delayed, duration, condition)

    /**
     * 清除指定标签下所有的[timer]、[periodic]任务
     * @param tag 指定标签
     */
    fun clearTasks(tag: Any?) = handleDelegate.clearTasks(tag)

    /**
     * 清除所有的[timer]、[periodic]任务
     */
    fun clearTasks() = handleDelegate.clearTasks()
}