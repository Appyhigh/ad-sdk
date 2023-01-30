package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListener
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListenerInternal
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class AppOpenAdManager : Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var adName: String? = null
    private var adUnit: String? = null
    private var appOpenAdLoadListener: AppOpenAdLoadListenerInternal? = null

    fun init(
        application: Application,
        adName: String,
        adUnit: String,
        appOpenAdLoadListener: AppOpenAdLoadListenerInternal?
    ) {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        this.adName = adName
        this.adUnit = adUnit
        this.appOpenAdLoadListener = appOpenAdLoadListener
    }

    @SuppressLint("VisibleForTests")
    fun loadAd(
        context: Context,
        adName: String?,
        adUnit: String?
    ) {
        this.adName = adName
        this.adUnit = adUnit
        if (isLoadingAd || isAdAvailable()) {
            return
        }
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context, adUnit!!, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    // Called when an app open ad has loaded.
                    loadTime = Date().time
                    appOpenAd = ad
                    isLoadingAd = false
                    appOpenAdLoadListener?.onAdLoaded(ad)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Called when an app open ad has failed to load.
                    isLoadingAd = false
                    appOpenAdLoadListener?.onAdFailedToLoad(loadAdError)
                }
            })
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }


    /** Shows the ad if one isn't already showing. */
    private fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity, adName, adUnit)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {


            override fun onAdDismissedFullScreenContent() {
                // Called when full screen content is dismissed.
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false

                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity, adName, adUnit)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false

                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity, adName, adUnit)
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                isShowingAd = true
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let {
            showAdIfAvailable(it)
        }
    }


    /** Show the ad if one isn't already showing. */
    private fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {

                }
            })
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4
    }

}