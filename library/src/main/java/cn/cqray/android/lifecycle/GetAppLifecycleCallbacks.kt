package cn.cqray.android.lifecycle

import android.app.Activity

/**
 * App生命周期回调
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetAppLifecycleCallbacks {
    /**
     * App在前台
     * @param activity 当前Activity
     */
    fun onForeground(activity: Activity) {}

    /**
     * App在后台
     * @param activity 当前Activity
     */
    fun onBackground(activity: Activity) {}

    /**
     * APP被创建
     */
    fun onCreated() {}

    /**
     * APP被回收
     */
    fun onTerminated() {}
}