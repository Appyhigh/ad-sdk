package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class PreloadMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preload_main)
        AdSdk.preloadAd(
            context = this,
            adName = "small_banner_admob"
        )

        AdSdk.preloadAd(
            context = this,
            adName = "native_small_admob"
        )

        AdSdk.preloadAd(
            context = this,
            adName = "banner_admanager"
        )

        AdSdk.preloadAd(
            context = this,
            adName = "native_small_admanager"
        )


        AdSdk.preloadAd(
            context = this,
            adName = "banner_applovin"
        )

        AdSdk.preloadAd(
            context = this,
            adName = "native_small_applovin"
        )

        findViewById<AppCompatButton>(R.id.bannerAdButton).setOnClickListener {
            start(BannerAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.bannerAdManagerAdButton).setOnClickListener {
            start(BannerAdManagerAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.bannerAppLovinAdButton).setOnClickListener {
            start(BannerApplovinAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.nativeAdButton).setOnClickListener {
            start(NativeAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.nativeAdManagerAdButton).setOnClickListener {
            start(NativeAdManagerAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.nativeAppLovinAdButton).setOnClickListener {
            start(NativeAppLovinAdExampleActivity())
        }


    }
}