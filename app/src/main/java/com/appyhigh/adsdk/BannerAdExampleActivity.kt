package com.appyhigh.adsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd

class BannerAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var smallTestBannerName = "small_banner_admob"
    private var mediumTestBannerName = "medium_banner_admob"
    private var largeTestBannerName = "large_banner_admob"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_ad_example)
        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = smallTestBannerName,
            parentView = findViewById(R.id.banner_container),
            showShimmerLoading = false
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
            parentView = findViewById(R.id.banner_container_large),
            showShimmerLoading = false
        )
    }
}