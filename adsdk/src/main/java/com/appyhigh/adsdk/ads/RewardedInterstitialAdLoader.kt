package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.interfaces.RewardedInterstitialAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

internal class RewardedInterstitialAdLoader {

    var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var adRequestsCompleted = 0
    private var adUnits = ArrayList<String>()
    private var countDownTimer: CountDownTimer? = null
    private var isAdLoaded = false
    private var adFailureReasonArray = ArrayList<String>()

    fun loadInterstitialRewardedAd(
        context: Context,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        rewardedInterstitialAdLoadListener: RewardedInterstitialAdLoadListener?
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
                        rewardedInterstitialAdLoadListener
                    )
                } else {
                    rewardedInterstitialAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                }
            }
        }

        requestAd(
            adName,
            adUnits[adRequestsCompleted],
            context,
            countDownTimer,
            rewardedInterstitialAdLoadListener
        )

    }

    @SuppressLint("VisibleForTests")
    fun requestAd(
        adName: String,
        adUnit: String,
        context: Context,
        countDownTimer: CountDownTimer?,
        rewardedInterstitialAdLoadListener: RewardedInterstitialAdLoadListener?
    ) {
        countDownTimer?.start()

        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            context,
            adUnit,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
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
                            rewardedInterstitialAdLoadListener
                        )
                    } else {
                        rewardedInterstitialAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                    }
                }

                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    if (!isAdLoaded) {
                        countDownTimer?.cancel()
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName === $adUnit ==== Interstitial Ad Loaded"
                        )
                        rewardedInterstitialAd = ad

                        rewardedInterstitialAdLoadListener?.onAdLoaded(ad)
                        isAdLoaded = true
                    }
                }
            })
    }
}