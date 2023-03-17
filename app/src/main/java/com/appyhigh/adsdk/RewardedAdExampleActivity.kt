package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.RewardedAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewarded.RewardedAd

class RewardedAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var rewardedAdName = "rewarded_admob"
    private var rewardedAdManagerAdName = "rewarded_admanager"
    private var rewardedApplovinAdName = "rewarded_applovin"
    private var mRewardedAd: RewardedAd? = null
    private var mMaxRewardedAd: MaxRewardedAd? = null
    private var loadRewardedAdButton: AppCompatButton? = null
    private var adProvider = "admob"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_ad_example)
        adProvider = intent.getStringExtra("adProvider").toString()
        loadRewardedAdButton = findViewById(R.id.show_rewarded_ad)
        loadRewardedAd()
        loadRewardedAdButton?.setOnClickListener {
            mMaxRewardedAd?.showAd()
            mMaxRewardedAd?.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd?) {
                }

                override fun onAdDisplayed(p0: MaxAd?) {
                }

                override fun onAdHidden(p0: MaxAd?) {
                    loadRewardedAd()
                }

                override fun onAdClicked(p0: MaxAd?) {
                }

                override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                }

                override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                    loadRewardedAd()
                }

                override fun onUserRewarded(p0: MaxAd?, p1: MaxReward?) {
                    println("${AdSdkConstants.TAG} ${p1?.label} ${p1?.amount}")
                }

                override fun onRewardedVideoStarted(p0: MaxAd?) {
                }

                override fun onRewardedVideoCompleted(p0: MaxAd?) {
                }
            })
            mRewardedAd?.show(this@RewardedAdExampleActivity) {
                println("${AdSdkConstants.TAG} ${it.type} ${it.amount}")
            }
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    loadRewardedAd()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    mRewardedAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    loadRewardedAd()
                }
            }
        }
    }

    private fun loadRewardedAd() {
        loadRewardedAdButton?.isEnabled = false
        AdSdk.loadAd(
            context = this,
            activity = this,
            lifecycle = lifecycle,
            adName = when (adProvider) {
                "admob" -> rewardedAdName
                "admanager" -> rewardedAdManagerAdName
                else -> rewardedApplovinAdName
            },
            rewardedAdLoadListener = object : RewardedAdLoadListener() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd = rewardedAd
                    loadRewardedAdButton?.isEnabled = true
                }

                override fun onMaxAdLoaded(rewardedAd: MaxRewardedAd) {
                    super.onMaxAdLoaded(rewardedAd)
                    mMaxRewardedAd = rewardedAd
                    loadRewardedAdButton?.isEnabled = true
                }

                override fun onAdFailedToLoad(adErrors: List<String>) {
                    super.onAdFailedToLoad(adErrors)
                    for (adError in adErrors) {
                        println("${AdSdkConstants.TAG} $adError")
                    }
                }
            }
        )
    }
}