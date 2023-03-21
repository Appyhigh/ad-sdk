package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd

class NativeAdManagerAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var testNativeAdName = "native_small_admanager"
    private var testNativeAdMediumName = "native_medium_admanager"
    private var testNativeAdBigv1Name = "native_bigv1_admanager"
    private var testNativeAdBigv2Name = "native_bigv2_admanager"
    private var testNativeAdBigv3Name = "native_bigv3_admanager"
    private var testNativeAdGridName = "native_grid_admanager"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_ad_example)
        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdName,
            parentView = findViewById(R.id.native_container)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdName,
            parentView = findViewById(R.id.native_container_dark),
            isDarkModeEnabled = true
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdMediumName,
            parentView = findViewById(R.id.native_container_medium)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdMediumName,
            parentView = findViewById(R.id.native_container_medium_dark),
            isDarkModeEnabled = true
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigv1Name,
            parentView = findViewById(R.id.banner_container_bigv1)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigv1Name,
            parentView = findViewById(R.id.banner_container_bigv1_dark),
            isDarkModeEnabled = true
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigv2Name,
            parentView = findViewById(R.id.banner_container_bigv2)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigv2Name,
            parentView = findViewById(R.id.banner_container_bigv2_dark),
            isDarkModeEnabled = true
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigv3Name,
            parentView = findViewById(R.id.banner_container_bigv3)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdBigv3Name,
            parentView = findViewById(R.id.banner_container_bigv3_dark),
            isDarkModeEnabled = true
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdGridName,
            parentView = findViewById(R.id.banner_container_grid)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdGridName,
            parentView = findViewById(R.id.banner_container_grid_dark),
            isDarkModeEnabled = true
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdGridName,
            parentView = findViewById(R.id.banner_container_grid2)
        )

        AdSdk.loadAd(
            context = this,
            lifecycle = lifecycle,
            adName = testNativeAdGridName,
            parentView = findViewById(R.id.banner_container_grid2_dark),
            isDarkModeEnabled = true
        )
    }
}