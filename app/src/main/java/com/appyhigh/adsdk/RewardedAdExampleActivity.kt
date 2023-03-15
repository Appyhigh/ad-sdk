package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.RewardedAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewarded.RewardedAd

class RewardedAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var rewardedAdName = "test_rewarded"
    private var mRewardedAd: RewardedAd? = null
    private var loadRewardedAdButton: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_ad_example)
        loadRewardedAdButton = findViewById(R.id.show_rewarded_ad)
        loadRewardedAd()
        loadRewardedAdButton?.setOnClickListener {
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
            lifecycle = lifecycle,
            adName = rewardedAdName,
            rewardedAdLoadListener = object : RewardedAdLoadListener() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd = rewardedAd
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