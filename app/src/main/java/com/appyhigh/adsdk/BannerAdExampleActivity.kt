package com.appyhigh.adsdk

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd

class BannerAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var applovinBannerName = "test_banner_app_lovin"
    private var smallTestBannerName = "test_banner"
    private var mediumTestBannerName = "test_banner_medium"
    private var largeTestBannerName = "test_banner_large"
    private var adView: MaxAdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_ad_example)

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = applovinBannerName,
            parentView = findViewById(R.id.banner_container_app_lovin)
        )

//        AdSdk.loadAd(
//            context = this,
//            lifecycle = lifecycle,
//            adName = smallTestBannerName,
//            parentView = findViewById(R.id.banner_container)
//        )
//
//        AdSdk.loadAd(
//            context = this,
//            lifecycle = lifecycle,
//            adName = mediumTestBannerName,
//            parentView = findViewById(R.id.banner_container_medium)
//        )
//        AdSdk.loadAd(
//            context = this,
//            lifecycle = lifecycle,
//            adName = largeTestBannerName,
//            parentView = findViewById(R.id.banner_container_large)
//        )
    }
}