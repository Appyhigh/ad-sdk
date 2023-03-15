package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd

class NativeAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var testNativeAdName = "test_native_ad"
    private var testNativeAdMediumName = "test_native_ad_medium"
    private var testNativeAdBigv1Name = "test_native_ad_bigv1"
    private var testNativeAdBigv2Name = "test_native_ad_bigv2"
    private var testNativeAdBigv3Name = "test_native_ad_bigv3"
    private var testNativeAdGridName = "test_native_ad_grid"
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