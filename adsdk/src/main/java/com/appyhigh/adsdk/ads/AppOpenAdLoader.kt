package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.core.os.bundleOf
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.AdSdkConstants.consentDisabledBundle
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListener
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListenerInternal
import com.appyhigh.adsdk.utils.Logger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*
import kotlin.collections.ArrayList

internal class AppOpenAdLoader {

    private var appOpenAdManager: AppOpenAdManager? = null
    private var adUnits = ArrayList<String>()
    private var adRequestsCompleted = 0
    private var adFailureReasonArray = ArrayList<String>()
    private var countDownTimer: CountDownTimer? = null
    private var appOpenAd: AppOpenAd? = null
    private var isAdLoaded = false

    fun loadAppOpenAd(
        context: Context,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        appOpenAdLoadListener: AppOpenAdLoadListener? = null
    ) {
        adUnits.addAll(primaryAdUnitIds)
        adUnits.addAll(secondaryAdUnitIds)
        adUnits.add(fallBackId)

        countDownTimer = object : CountDownTimer(timeout.toLong(), timeout.toLong()) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                val error =
                    "$adName ==== ${adUnits[adRequestsCompleted]} ==== ${context.getString(R.string.error_app_open_timed_out)}"
                Logger.e(AdSdkConstants.TAG, error)
                adFailureReasonArray.add(error)
                adRequestsCompleted += 1
                if (adRequestsCompleted < adUnits.size) {
                    requestAppOpenAd(
                        context,
                        adName,
                        adUnits[adRequestsCompleted],
                        countDownTimer,
                        appOpenAdLoadListener
                    )
                } else {
                    appOpenAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                }
            }
        }
        requestAppOpenAd(
            context,
            adName,
            adUnits[adRequestsCompleted],
            countDownTimer,
            appOpenAdLoadListener
        )
    }

    fun loadAppOpenAdBgToFg(
        application: Application,
        context: Context,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        appOpenAdLoadListener: AppOpenAdLoadListener? = null
    ) {
        adUnits.addAll(primaryAdUnitIds)
        adUnits.addAll(secondaryAdUnitIds)
        adUnits.add(fallBackId)

        appOpenAdManager = AppOpenAdManager()
        appOpenAdManager?.init(
            application,
            adName,
            fallBackId,
            object : AppOpenAdLoadListenerInternal() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    val error =
                        "$adName ==== ${adUnits[adRequestsCompleted]} ==== ${loadAdError.message}"
                    Logger.e(AdSdkConstants.TAG, error)
                    adFailureReasonArray.add(error)
                    adRequestsCompleted += 1
                    if (adRequestsCompleted < adUnits.size) {
                        requestAd(context, adName, adUnits[adRequestsCompleted])
                    } else {
                        appOpenAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                    }
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    super.onAdLoaded(ad)
                    appOpenAdLoadListener?.onAdLoaded(ad)
                }
            })
        requestAd(context, adName, adUnits[adRequestsCompleted])
        appOpenAdLoadListener?.onInitSuccess(appOpenAdManager)
    }

    private fun requestAd(
        context: Context,
        adName: String,
        adUnit: String
    ) {
        appOpenAdManager?.loadAd(
            context,
            adName,
            adUnit
        )
    }

    @SuppressLint("VisibleForTests")
    fun requestAppOpenAd(
        context: Context,
        adName: String?,
        adUnit: String?,
        countDownTimer: CountDownTimer?,
        appOpenAdLoadListener: AppOpenAdLoadListener?
    ) {
        val request = AdRequest.Builder().addNetworkExtrasBundle(
            AdMobAdapter::class.java,
            if (!AdSdkConstants.consentStatus) consentDisabledBundle else bundleOf()
        ).build()
        AppOpenAd.load(
            context, adUnit!!, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    if (!isAdLoaded) {
                        countDownTimer?.cancel()
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnit ==== ${context.getString(R.string.app_open_loaded)}"
                        )
                        appOpenAd = ad

                        appOpenAdLoadListener?.onAdLoaded(ad)
                        isAdLoaded = true
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    countDownTimer?.cancel()
                    val error = "$adName ==== $adUnit ==== ${loadAdError.message}"
                    adFailureReasonArray.add(error)
                    Logger.e(AdSdkConstants.TAG, error)
                    adRequestsCompleted += 1
                    if (adRequestsCompleted < adUnits.size) {
                        requestAppOpenAd(
                            context,
                            adName,
                            adUnits[adRequestsCompleted],
                            countDownTimer,
                            appOpenAdLoadListener
                        )
                    } else {
                        appOpenAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                    }
                }
            })
    }

}