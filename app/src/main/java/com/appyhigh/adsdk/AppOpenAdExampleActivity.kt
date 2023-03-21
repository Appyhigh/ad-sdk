package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.appyhigh.adsdk.data.enums.AppOpenLoadType
import com.appyhigh.adsdk.interfaces.AppOpenAdLoadListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenAdExampleActivity : AppCompatActivity() {
    private var appOpenAd: AppOpenAd? = null
    private var maxAppOpenAd: MaxAppOpenAd? = null
    private var appOpenAdName = "app_open_admob"
    private var appOpenAdManagerName = "app_open_admanager"
    private var appOpenApplovinName = "app_open_applovin"
    private var showAppOpenButton: AppCompatButton? = null
    private var adProvider = "admob"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_open_ad_example)
        adProvider = intent.getStringExtra("adProvider").toString()
        loadAppOpenAd()
//        loadAppOpenAdBgToFg()
        showAppOpenButton = findViewById(R.id.show_app_open_ad)
        showAppOpenButton?.setOnClickListener {
            if (adProvider == "applovin") {
                maxAppOpenAd?.showAd()
                maxAppOpenAd?.setListener(object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd?) {
                    }

                    override fun onAdDisplayed(p0: MaxAd?) {
                    }

                    override fun onAdHidden(p0: MaxAd?) {
                        loadAppOpenAd()
                    }

                    override fun onAdClicked(p0: MaxAd?) {
                    }

                    override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                    }

                    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                        loadAppOpenAd()
                    }

                })
            } else {
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
    }

    private fun loadAppOpenAdBgToFg() {
        AdSdk.loadAd(
            application = application,
            context = this,
            adName = when (adProvider) {
                "admob" -> appOpenAdName
                "admanager" -> appOpenAdManagerName
                else -> appOpenApplovinName
            },
            appOpenLoadType = AppOpenLoadType.BACKGROUND_TO_FOREGROUND
        )
    }

    private fun loadAppOpenAd() {
        AdSdk.loadAd(
            application = application,
            context = this,
            adName = when (adProvider) {
                "admob" -> appOpenAdName
                "admanager" -> appOpenAdManagerName
                else -> appOpenApplovinName
            },
            appOpenLoadType = AppOpenLoadType.SINGLE_LOAD,
            appOpenAdLoadListener = object : AppOpenAdLoadListener() {
                override fun onAdFailedToLoad(loadAdError: List<String>) {
                    super.onAdFailedToLoad(loadAdError)
                    for (adError in loadAdError) {
                        println("${AdSdkConstants.TAG} $adError")
                    }
                }

                override fun onApplovinAdLoaded(ad: MaxAppOpenAd) {
                    super.onApplovinAdLoaded(ad)
                    maxAppOpenAd = ad
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    super.onAdLoaded(ad)
                    appOpenAd = ad
                }
            }
        )
    }
}