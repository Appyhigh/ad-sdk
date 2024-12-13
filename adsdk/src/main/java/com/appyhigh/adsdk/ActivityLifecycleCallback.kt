package com.appyhigh.adsdk

import android.app.Activity
import android.app.Application
import android.os.Bundle

interface ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityPreStarted(activity: Activity) {}
    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityPostStarted(activity: Activity) {}
    override fun onActivityPreResumed(activity: Activity) {}
    override fun onActivityPostResumed(activity: Activity) {}
    override fun onActivityPrePaused(activity: Activity) {}
    override fun onActivityPaused(p0: Activity) {}
    override fun onActivityPostPaused(activity: Activity) {}
    override fun onActivityPreStopped(activity: Activity) {}
    override fun onActivityStopped(p0: Activity) {}
    override fun onActivityPostStopped(activity: Activity) {}
    override fun onActivityPreSaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityPostSaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityPreDestroyed(activity: Activity) {}
    override fun onActivityDestroyed(p0: Activity) {}
    override fun onActivityPostDestroyed(activity: Activity) {}
}