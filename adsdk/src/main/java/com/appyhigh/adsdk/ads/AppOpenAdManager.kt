package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.*
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListenerInternal
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.utils.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class AppOpenAdManager : Application.ActivityLifecycleCallbacks, LifecycleEventObserver {
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
                    loadTime = Date().time
                    appOpenAd = ad
                    isLoadingAd = false
                    appOpenAdLoadListener?.onAdLoaded(ad)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
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

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    private fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        if (isShowingAd) {
            Logger.d(AdSdkConstants.TAG, "App Open Ad Already Showing")
            return
        }
        if (currentActivity is BypassAppOpenAd) {
            Logger.d(AdSdkConstants.TAG, "App Open Ad Display Bypassed")
            return
        }

        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity, adName, adUnit)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity, adName, adUnit)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity, adName, adUnit)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }

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

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            currentActivity?.let {
                showAdIfAvailable(it)
            }
        }
    }

}