package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd

class BannerAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var smallTestBannerName = "test_banner"
    private var mediumTestBannerName = "test_banner_medium"
    private var largeTestBannerName = "test_banner_large"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_ad_example)
        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = smallTestBannerName,
            parentView = findViewById(R.id.banner_container)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = mediumTestBannerName,
            parentView = findViewById(R.id.banner_container_medium)
        )
        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = largeTestBannerName,
            parentView = findViewById(R.id.banner_container_large)
        )
    }
}