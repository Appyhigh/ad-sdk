package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.appyhigh.adsdk.data.enums.AppOpenLoadType
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenAdExampleActivity : AppCompatActivity() {
    private var appOpenAd: AppOpenAd? = null
    private var appOpenBgToBgAd: AppOpenAd? = null
    private var appOpenAdName = "test_app_open"
    private var appOpenAdBgtoFgName = "test_app_open_bg_to_fg"
    private var showAppOpenButton: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_open_ad_example)
        loadAppOpenAd()
        loadAppOpenAdBgToFg()
        showAppOpenButton = findViewById(R.id.show_app_open_ad)
        showAppOpenButton?.setOnClickListener {
            appOpenAd?.show(this@AppOpenAdExampleActivity)
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    loadAppOpenAd()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    loadAppOpenAd()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    appOpenAd = null
                }
            }
        }
    }

    private fun loadAppOpenAdBgToFg() {
        AdSdk.loadAd(
            application = application,
            context = this,
            adName = appOpenAdBgtoFgName,
            appOpenLoadType = AppOpenLoadType.BACKGROUND_TO_FOREGROUND
        )
    }

    private fun loadAppOpenAd() {
        AdSdk.loadAd(
            application = application,
            context = this,
            adName = appOpenAdName,
            appOpenLoadType = AppOpenLoadType.SINGLE_LOAD,
            appOpenAdLoadListener = object : AppOpenAdLoadListener() {
                override fun onAdFailedToLoad(loadAdError: List<String>) {
                    super.onAdFailedToLoad(loadAdError)
                    for (adError in loadAdError) {
                        println("${AdSdkConstants.TAG} $adError")
                    }
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    super.onAdLoaded(ad)
                    appOpenAd = ad
                }
            }
        )
    }
}