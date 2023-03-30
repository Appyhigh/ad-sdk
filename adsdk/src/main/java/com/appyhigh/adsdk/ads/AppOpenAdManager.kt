package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.lifecycle.*
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.AdSdkConstants.consentDisabledBundle
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.data.enums.AdProvider
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListenerInternal
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.utils.Logger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
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
    private var adUnitProvider: String? = null
    private var backgroundThreshold: Long = 4000
    private var appOpenAdLoadListener: AppOpenAdLoadListenerInternal? = null
    private var appCount = 0
    private var backgroundTime: Long = 0
    private var isPremium: Boolean = false

    companion object {
        var isPremiumUser: Boolean = false
    }

    fun init(
        application: Application,
        adName: String,
        adUnit: String,
        appOpenAdLoadListener: AppOpenAdLoadListenerInternal?,
        backgroundThreshold: Int,
    ) {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        this.backgroundThreshold = backgroundThreshold.toLong()
        this.adName = adName
        this.adUnit = adUnit
        this.adUnitProvider = adUnitProvider
        this.appOpenAdLoadListener = appOpenAdLoadListener
    }

    @SuppressLint("VisibleForTests")
    fun loadAd(
        context: Context,
        adName: String?,
        adUnit: String?,
        adUnitProvider: String?
    ) {
        this.adName = adName
        this.adUnit = adUnit
        this.adUnitProvider = adUnitProvider
        if (isLoadingAd || isAdAvailable()) {
            return
        }
        isLoadingAd = true
        val request = if (adUnitProvider == AdProvider.ADMOB.name.lowercase()) {
            AdRequest.Builder()
        } else {
            AdManagerAdRequest.Builder()
        }

        request.addNetworkExtrasBundle(
            AdMobAdapter::class.java,
            if (!AdSdkConstants.consentStatus) consentDisabledBundle else bundleOf()
        )

        AppOpenAd.load(
            context, adUnit!!, request.build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    Logger.d(
                        AdSdkConstants.TAG,
                        "$adName ==== $adUnit ==== ${context.getString(R.string.app_open_loaded)}"
                    )
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
            Logger.d(
                AdSdkConstants.TAG,
                activity.getString(R.string.error_app_open_already_showing)
            )
            return
        }
        if (currentActivity is BypassAppOpenAd) {
            Logger.d(AdSdkConstants.TAG, activity.getString(R.string.error_app_open_bypassed))
            return
        }

        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity, adName, adUnit, adUnitProvider)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity, adName, adUnit, adUnitProvider)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity, adName, adUnit, adUnitProvider)
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
        Handler(Looper.getMainLooper()).postDelayed({
            if (event == Lifecycle.Event.ON_START) {
                if (appCount > 0 && !currentActivity.toString()
                        .contains("CallerIdActivity") && !currentActivity.toString()
                        .contains("CallActivity") && !currentActivity.toString()
                        .contains("AdActivity")
                ) {
                    val appBackgroundTime = System.currentTimeMillis() - backgroundTime
                    isPremium = isPremiumUser
                    if (appBackgroundTime > backgroundThreshold && !isPremium)
                        currentActivity?.let {
                            showAdIfAvailable(it)
                        }
                }
            }
            if (event == Lifecycle.Event.ON_STOP) {
                backgroundTime = System.currentTimeMillis()
            }
            appCount++
        }, 300)
    }

}