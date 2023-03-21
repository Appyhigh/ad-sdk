package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import androidx.core.os.bundleOf
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.AdSdkConstants.consentDisabledBundle
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.interfaces.RewardedAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

internal class RewardedAdLoader {

    private var rewardedAd: RewardedAd? = null
    private var adRequestsCompleted = 0
    private var adUnits = ArrayList<String>()
    private var adUnitsProvider = ArrayList<String>()
    private var countDownTimer: CountDownTimer? = null
    private var isAdLoaded = false
    private var adFailureReasonArray = ArrayList<String>()

    fun loadRewardedAd(
        context: Context,
        activity: Activity,
        adName: String,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        primaryAdUnitProvider: String,
        secondaryAdUnitProvider: String,
        timeout: Int,
        rewardedAdLoadListener: RewardedAdLoadListener?
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
        adUnitsProvider.add("admob")

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
                        activity,
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
            activity,
            countDownTimer,
            rewardedAdLoadListener
        )

    }

    @SuppressLint("VisibleForTests")
    fun requestAd(
        adName: String,
        adUnit: String,
        context: Context,
        activity: Activity,
        countDownTimer: CountDownTimer?,
        rewardedAdLoadListener: RewardedAdLoadListener?
    ) {
        countDownTimer?.start()
        if (adUnitsProvider[adRequestsCompleted] == "applovin") {
            val rewardedAd = MaxRewardedAd.getInstance(adUnit, activity)
            rewardedAd.loadAd()
            rewardedAd.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd?) {
                    adLoadedCallback(
                        adName,
                        adUnit,
                        context,
                        countDownTimer,
                        rewardedAdLoadListener,
                        null,
                        rewardedAd
                    )
                }

                override fun onAdDisplayed(p0: MaxAd?) {
                }

                override fun onAdHidden(p0: MaxAd?) {
                }

                override fun onAdClicked(p0: MaxAd?) {
                }

                override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                }

                override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                    failedAdCallback(
                        adName,
                        adUnit,
                        context,
                        activity,
                        countDownTimer,
                        rewardedAdLoadListener,
                        p1?.message
                    )
                }

                override fun onUserRewarded(p0: MaxAd?, p1: MaxReward?) {
                }

                @Deprecated("Deprecated in Java")
                override fun onRewardedVideoStarted(p0: MaxAd?) {
                }

                @Deprecated("Deprecated in Java")
                override fun onRewardedVideoCompleted(p0: MaxAd?) {
                }
            })
        } else {
            val adRequest = if (adUnitsProvider[adRequestsCompleted] == "admob") {
                AdRequest.Builder()
            } else {
                AdManagerAdRequest.Builder()
            }
            adRequest.addNetworkExtrasBundle(
                AdMobAdapter::class.java,
                if (!AdSdkConstants.consentStatus) consentDisabledBundle else bundleOf()
            )
            RewardedAd.load(
                context,
                adUnit,
                adRequest.build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        failedAdCallback(
                            adName,
                            adUnit,
                            context,
                            activity,
                            countDownTimer,
                            rewardedAdLoadListener,
                            adError.message
                        )
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        adLoadedCallback(
                            adName,
                            adUnit,
                            context,
                            countDownTimer,
                            rewardedAdLoadListener,
                            ad,
                            null
                        )
                    }
                })
        }
    }

    private fun adLoadedCallback(
        adName: String,
        adUnit: String,
        context: Context,
        countDownTimer: CountDownTimer?,
        rewardedAdLoadListener: RewardedAdLoadListener?,
        ad: RewardedAd?,
        maxAd: MaxRewardedAd?
    ) {
        if (!isAdLoaded) {
            countDownTimer?.cancel()
            Logger.d(
                AdSdkConstants.TAG,
                "$adName ==== $adUnit ==== ${context.getString(R.string.rewarded_ad_loaded)}"
            )
            rewardedAd = ad
            if (ad != null)
                rewardedAdLoadListener?.onAdLoaded(ad)
            if (maxAd != null)
                rewardedAdLoadListener?.onMaxAdLoaded(maxAd)
            isAdLoaded = true
        }
    }

    private fun failedAdCallback(
        adName: String,
        adUnit: String,
        context: Context,
        activity: Activity,
        countDownTimer: CountDownTimer?,
        rewardedAdLoadListener: RewardedAdLoadListener?,
        message: String?
    ) {
        countDownTimer?.cancel()
        val error = "$adName ==== $adUnit ==== $message"
        adFailureReasonArray.add(error)
        Logger.e(AdSdkConstants.TAG, error)
        adRequestsCompleted += 1
        if (adRequestsCompleted < adUnits.size) {
            requestAd(
                adName,
                adUnits[adRequestsCompleted],
                context,
                activity,
                countDownTimer,
                rewardedAdLoadListener
            )
        } else {
            rewardedAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
        }
    }
}