package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.InterstitialAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd

class InterstitialAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var testInterstitialName = "test_interstitial"
    private var mInterstitialAd: InterstitialAd? = null
    private var loadInterstitialAdButton: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial_ad_example)
        loadInterstitialAdButton = findViewById(R.id.show_interstitial_ad)
        loadInterstitialAd()
        loadInterstitialAdButton?.setOnClickListener {
            mInterstitialAd?.show(this)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    loadInterstitialAd()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    loadInterstitialAd()
                }
            }
        }
    }

    private fun loadInterstitialAd() {
        loadInterstitialAdButton?.isEnabled = false
        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testInterstitialName,
            interstitialAdLoadListener = object : InterstitialAdLoadListener() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                    loadInterstitialAdButton?.isEnabled = true
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