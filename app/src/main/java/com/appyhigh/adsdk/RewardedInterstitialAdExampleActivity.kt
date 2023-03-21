package com.appyhigh.adsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.RewardedInterstitialAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

class RewardedInterstitialAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var rewardedAdName = "rewarded_interstitial_admob"
    private var testInterstitialAdManagerName = "rewarded_interstitial_admanager"
    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    private var loadRewardedInterstitialAdButton: AppCompatButton? = null
    private var adProvider = "admob"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_interstitial_ad_example)
        adProvider = intent.getStringExtra("adProvider").toString()
        loadRewardedInterstitialAdButton = findViewById(R.id.show_rewarded_interstitial_ad)
        loadRewardedInterstitialAd()
        loadRewardedInterstitialAdButton?.setOnClickListener {
            mRewardedInterstitialAd?.show(this@RewardedInterstitialAdExampleActivity) {
                println("${AdSdkConstants.TAG} ${it.type} ${it.amount}")
            }
            mRewardedInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        loadRewardedInterstitialAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        mRewardedInterstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        loadRewardedInterstitialAd()
                    }
                }
        }
    }

    private fun loadRewardedInterstitialAd() {
        loadRewardedInterstitialAdButton?.isEnabled = false
        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = when (adProvider) {
                "admob" -> rewardedAdName
                "admanager" -> testInterstitialAdManagerName
                else -> rewardedAdName
            },
            rewardedInterstitialAdLoadListener = object : RewardedInterstitialAdLoadListener() {
                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    super.onAdLoaded(rewardedInterstitialAd)
                    mRewardedInterstitialAd = rewardedInterstitialAd
                    loadRewardedInterstitialAdButton?.isEnabled = true
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