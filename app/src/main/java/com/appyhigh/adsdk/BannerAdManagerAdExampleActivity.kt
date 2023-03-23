package com.appyhigh.adsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BannerAdManagerAdExampleActivity : AppCompatActivity() {
    private var smallTestBannerName = "banner_admanager"
    private var mediumTestBannerName = "banner_medium_admanager"
    private var largeTestBannerName = "banner_large_admanager"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_ad_manager_ad_example)
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