package com.appyhigh.adsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BannerApplovinAdExampleActivity : AppCompatActivity() {
    private var smallTestBannerName = "banner_applovin"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_applovin_ad_example)

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = smallTestBannerName,
            parentView = findViewById(R.id.banner_container)
        )
    }
}