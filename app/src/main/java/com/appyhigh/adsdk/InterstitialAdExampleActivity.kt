package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.InterstitialAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd

class InterstitialAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var testInterstitialName = "test_interstitial"
    private var testApplovinInterstitialName = "test_interstitial_app_lovin"
    private var mInterstitialAd: InterstitialAd? = null
    private var mMaxInterstitialAd: MaxInterstitialAd? = null
    private var loadInterstitialAdButton: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial_ad_example)
        loadInterstitialAdButton = findViewById(R.id.show_interstitial_ad)
        loadInterstitialAd()
        loadInterstitialAdButton?.setOnClickListener {
            if (mMaxInterstitialAd?.isReady!!) {
                mMaxInterstitialAd?.showAd()
                mMaxInterstitialAd?.setListener(object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd?) {
                    }

                    override fun onAdDisplayed(p0: MaxAd?) {
                    }

                    override fun onAdHidden(p0: MaxAd?) {
                        loadInterstitialAd()
                    }

                    override fun onAdClicked(p0: MaxAd?) {
                    }

                    override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                    }

                    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                        loadInterstitialAd()
                    }
                })
            }
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
            activity = this,
            lifecycle = lifecycle,
            adName = testApplovinInterstitialName,
            interstitialAdLoadListener = object : InterstitialAdLoadListener() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                    loadInterstitialAdButton?.isEnabled = true
                }

                override fun onApplovinAdLoaded(interstitialAd: MaxInterstitialAd) {
                    super.onApplovinAdLoaded(interstitialAd)
                    mMaxInterstitialAd = interstitialAd
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