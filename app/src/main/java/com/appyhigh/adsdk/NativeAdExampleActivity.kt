package com.appyhigh.adsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.applovin.mediation.nativeAds.MaxNativeAd
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.NativeAdLoadListener
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdExampleActivity : AppCompatActivity(), BypassAppOpenAd {
    private var testNativeAdName = "native_small_admob"
    private var testNativeAdMediumName = "native_medium_admob"
    private var testNativeAdBigv1Name = "native_bigv1_admob"
    private var testNativeAdBigv2Name = "native_bigv2_admob"
    private var testNativeAdBigv3Name = "native_bigv3_admob"
    private var testNativeAdGridName = "native_grid_admob"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_ad_example)
        AdSdk.fetchNativeAd(
            context = this,
            adName = testNativeAdName,
            parentView = findViewById(R.id.native_container),
            nativeAdLoadListener = object : NativeAdLoadListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(errors: List<String>) {
                    super.onAdFailedToLoad(errors)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdInflated() {
                    super.onAdInflated()
                }

                override fun onAdLoaded(nativeAd: NativeAd?) {
                    super.onAdLoaded(nativeAd)
                    Log.d(AdSdkConstants.TAG, "onAdLoaded: $nativeAd")
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                }

                override fun onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked()
                }

                override fun onMaxAdLoaded(nativeAd: LinearLayout?) {
                    super.onMaxAdLoaded(nativeAd)
                }

                override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {
                    super.onMultipleAdsLoaded(nativeAds)
                    Log.d(AdSdkConstants.TAG, "onAdLoaded: $nativeAds")
                }
            }
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