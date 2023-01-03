package cn.cqray.android.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

import cn.cqray.android.Get

/**
 * [Get]生命周期回调
 * @author Cqray
 */
@JvmDefaultWithoutCompatibility
interface GetActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}