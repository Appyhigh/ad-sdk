package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.interfaces.InterstitialAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdLoader {
    var interstitialAd: InterstitialAd? = null
    private var adRequestsCompleted = 0
    private var adUnits = ArrayList<String>()
    private var currentAdUnitId: String? = null
    private var countDownTimer: CountDownTimer? = null
    private var adFailureReasonArray = ArrayList<String>()
    private var isAdLoaded = false

    fun loadInterstitialAd(
        context: Context,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        interstitialAdLoadListener: InterstitialAdLoadListener?
    ) {
        adUnits.addAll(primaryAdUnitIds)
        adUnits.addAll(secondaryAdUnitIds)
        adUnits.add(fallBackId)

        countDownTimer = object : CountDownTimer(timeout.toLong(), timeout.toLong()) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                val error =
                    "$adName ==== ${adUnits[adRequestsCompleted]} ==== Interstitial Ad Unit Timed out"
                Logger.e(AdSdkConstants.TAG, error)
                adFailureReasonArray.add(error)
                adRequestsCompleted += 1
                if (adRequestsCompleted < adUnits.size) {
                    requestAd(
                        adName,
                        adUnits[adRequestsCompleted],
                        context,
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
            context,
            countDownTimer,
            interstitialAdLoadListener
        )
    }

    @SuppressLint("VisibleForTests")
    private fun requestAd(
        adName: String,
        adUnit: String,
        context: Context,
        countDownTimer: CountDownTimer?,
        interstitialAdLoadListener: InterstitialAdLoadListener?
    ) {
        currentAdUnitId = adUnit
        countDownTimer?.start()
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnit,
            adRequest,
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
                            context,
                            countDownTimer,
                            interstitialAdLoadListener
                        )
                    } else {
                        interstitialAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    if (currentAdUnitId == ad.adUnitId) {
                        countDownTimer?.cancel()
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName === $adUnit ==== Interstitial Ad Loaded"
                        )
                        interstitialAd = ad
                        if (!isAdLoaded) {
                            interstitialAdLoadListener?.onAdLoaded(ad)
                        }
                        isAdLoaded = true
                    }
                }
            })
    }
}