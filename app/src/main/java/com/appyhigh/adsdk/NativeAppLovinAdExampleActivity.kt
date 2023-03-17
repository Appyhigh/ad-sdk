package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NativeAppLovinAdExampleActivity : AppCompatActivity() {
    private var testNativeAdName = "native_small_applovin"
    private var testNativeAdBigName = "native_big_applovin"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_app_lovin_ad_example)

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdName,
            parentView = findViewById(R.id.native_container)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigName,
            parentView = findViewById(R.id.native_container_medium),
        )
    }
}