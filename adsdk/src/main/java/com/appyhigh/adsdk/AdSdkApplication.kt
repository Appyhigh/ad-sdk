package com.appyhigh.adsdk

import android.app.Activity
import android.app.Application

open class AdSdkApplication : Application() {
    private var currentActivity: Activity? = null
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallback {
            override fun onActivityResumed(activity: Activity) {
                super.onActivityResumed(activity)
                currentActivity = activity
                AdSdk.showPopupAd(activity)
            }
        })
    }
}