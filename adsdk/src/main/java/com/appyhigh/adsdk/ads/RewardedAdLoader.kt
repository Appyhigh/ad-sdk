package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.interfaces.RewardedAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

internal class RewardedAdLoader {

    var rewardedAd: RewardedAd? = null
    private var adRequestsCompleted = 0
    private var adUnits = ArrayList<String>()
    private var countDownTimer: CountDownTimer? = null
    private var isAdLoaded = false
    private var adFailureReasonArray = ArrayList<String>()

    fun loadRewardedAd(
        context: Context,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        rewardedAdLoadListener: RewardedAdLoadListener?
    ) {

        adUnits.addAll(primaryAdUnitIds)
        adUnits.addAll(secondaryAdUnitIds)
        adUnits.add(fallBackId)

        countDownTimer = object : CountDownTimer(timeout.toLong(), timeout.toLong()) {
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                val error =
                    "$adName ==== ${adUnits[adRequestsCompleted]} ==== ${context.getString(R.string.error_rewarded_ad_timed_out)}"
                Logger.e(AdSdkConstants.TAG, error)
                adFailureReasonArray.add(error)
                adRequestsCompleted += 1
                if (adRequestsCompleted < adUnits.size) {
                    requestAd(
                        adName,
                        adUnits[adRequestsCompleted],
                        context,
                        countDownTimer,
                        rewardedAdLoadListener
                    )
                } else {
                    rewardedAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                }
            }
        }

        requestAd(
            adName,
            adUnits[adRequestsCompleted],
            context,
            countDownTimer,
            rewardedAdLoadListener
        )

    }

    @SuppressLint("VisibleForTests")
    fun requestAd(
        adName: String,
        adUnit: String,
        context: Context,
        countDownTimer: CountDownTimer?,
        rewardedAdLoadListener: RewardedAdLoadListener?
    ) {
        countDownTimer?.start()

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnit,
            adRequest,
            object : RewardedAdLoadCallback() {
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
                            rewardedAdLoadListener
                        )
                    } else {
                        rewardedAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                    }
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    if (!isAdLoaded) {
                        countDownTimer?.cancel()
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnit ==== ${context.getString(R.string.rewarded_ad_loaded)}"
                        )
                        rewardedAd = ad
                        rewardedAdLoadListener?.onAdLoaded(ad)
                        isAdLoaded = true
                    }
                }
            })
    }
}