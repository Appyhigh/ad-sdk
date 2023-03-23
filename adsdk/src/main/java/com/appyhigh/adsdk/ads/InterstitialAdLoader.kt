package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import androidx.core.os.bundleOf
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.AdSdkConstants.consentDisabledBundle
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.data.enums.AdProvider
import com.appyhigh.adsdk.interfaces.InterstitialAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

internal class InterstitialAdLoader {
    var interstitialAd: InterstitialAd? = null
    private var adRequestsCompleted = 0
    private var adUnits = ArrayList<String>()
    private var adUnitsProvider = ArrayList<String>()
    private var countDownTimer: CountDownTimer? = null
    private var adFailureReasonArray = ArrayList<String>()
    private var isAdLoaded = false

    fun loadInterstitialAd(
        activity: Activity,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        primaryAdUnitProvider: String,
        secondaryAdUnitProvider: String,
        timeout: Int,
        interstitialAdLoadListener: InterstitialAdLoadListener?
    ) {
        for (adUnit in primaryAdUnitIds) {
            adUnits.add(adUnit)
            adUnitsProvider.add(primaryAdUnitProvider)
        }

        for (adUnit in secondaryAdUnitIds) {
            adUnits.add(adUnit)
            adUnitsProvider.add(secondaryAdUnitProvider)
        }
        adUnits.add(fallBackId)
        adUnitsProvider.add(AdProvider.ADMOB.name.lowercase())

        countDownTimer = object : CountDownTimer(timeout.toLong(), timeout.toLong()) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {

                val error =
                    "$adName ==== ${adUnits[adRequestsCompleted]} ==== ${activity.getString(R.string.error_interstitial_timed_out)}"
                Logger.e(AdSdkConstants.TAG, error)
                adFailureReasonArray.add(error)
                adRequestsCompleted += 1
                if (adRequestsCompleted < adUnits.size) {
                    requestAd(
                        adName,
                        adUnits[adRequestsCompleted],
                        activity,
                        countDownTimer,
                        interstitialAdLoadListener
                    )
                } else {
                    interstitialAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                }
            }

        }
        requestAd(
            adName,
            adUnits[adRequestsCompleted],
            activity,
            countDownTimer,
            interstitialAdLoadListener
        )
    }

    @SuppressLint("VisibleForTests")
    private fun requestAd(
        adName: String,
        adUnit: String,
        activity: Activity,
        countDownTimer: CountDownTimer?,
        interstitialAdLoadListener: InterstitialAdLoadListener?
    ) {
        countDownTimer?.start()
        if (adUnitsProvider[adRequestsCompleted] == AdProvider.APPLOVIN.name.lowercase()) {
            val interstitialAd = MaxInterstitialAd(adUnit, activity)
            interstitialAd.loadAd()
            interstitialAd.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    if (!isAdLoaded) {
                        countDownTimer?.cancel()
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnit ==== ${activity.getString(R.string.interstitial_ad_loaded)}"
                        )
                        interstitialAdLoadListener?.onApplovinAdLoaded(interstitialAd)
                        isAdLoaded = true
                    }
                }

                override fun onAdDisplayed(p0: MaxAd?) {
                }

                override fun onAdHidden(p0: MaxAd?) {
                }

                override fun onAdClicked(p0: MaxAd?) {
                }

                override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                    countDownTimer?.cancel()
                    val error = "$adName ==== $adUnit ==== ${p1?.message}"
                    adFailureReasonArray.add(error)
                    Logger.e(AdSdkConstants.TAG, error)
                    adRequestsCompleted += 1
                    if (adRequestsCompleted < adUnits.size) {
                        requestAd(
                            adName,
                            adUnits[adRequestsCompleted],
                            activity,
                            countDownTimer,
                            interstitialAdLoadListener
                        )
                    } else {
                        interstitialAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                    }
                }

                override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                }

            })
        } else {
            val adRequest = if (adUnitsProvider[adRequestsCompleted] == AdProvider.ADMOB.name.lowercase()) {
                AdRequest.Builder()
            } else {
                AdManagerAdRequest.Builder()
            }
            adRequest.addNetworkExtrasBundle(
                AdMobAdapter::class.java,
                if (!AdSdkConstants.consentStatus) consentDisabledBundle else bundleOf()
            )
            InterstitialAd.load(
                activity,
                adUnit,
                adRequest.build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        countDownTimer?.cancel()
                        val error = "$adName ==== $adUnit ==== ${adError.message}"
                        adFailureReasonArray.add(error)
                        Logger.e(AdSdkConstants.TAG, error)
                        adRequestsCompleted += 1
                        if (adRequestsCompleted < adUnits.size) {
                            requestAd(
                                adName,
                                adUnits[adRequestsCompleted],
                                activity,
                                countDownTimer,
                                interstitialAdLoadListener
                            )
                        } else {
                            interstitialAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                        }
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        if (!isAdLoaded) {
                            countDownTimer?.cancel()
                            Logger.d(
                                AdSdkConstants.TAG,
                                "$adName ==== $adUnit ==== ${activity.getString(R.string.interstitial_ad_loaded)}"
                            )
                            interstitialAd = ad
                            interstitialAdLoadListener?.onAdLoaded(ad)
                            isAdLoaded = true
                        }
                    }
                })
        }
    }
}