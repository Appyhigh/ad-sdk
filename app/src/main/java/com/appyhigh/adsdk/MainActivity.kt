package com.appyhigh.adsdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appyhigh.adsdk.data.enums.AdProvider


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.preloadButton).setOnClickListener {
            start(PreloadMainActivity())
        }

        findViewById<AppCompatButton>(R.id.bannerAdButton).setOnClickListener {
            start(BannerAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.interstitialAdButton).setOnClickListener {
            start(InterstitialAdExampleActivity(), "admob")
        }

        findViewById<AppCompatButton>(R.id.interstitialAdManagerAdButton).setOnClickListener {
            start(InterstitialAdExampleActivity(), "admanager")
        }

        findViewById<AppCompatButton>(R.id.interstitialAppLovinAdButton).setOnClickListener {
            start(InterstitialAdExampleActivity(), AdProvider.APPLOVIN.name.lowercase())
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

        findViewById<AppCompatButton>(R.id.rewardedAdButton).setOnClickListener {
            start(RewardedAdExampleActivity(), "admob")
        }

        findViewById<AppCompatButton>(R.id.rewardedAdManagerAdButton).setOnClickListener {
            start(RewardedAdExampleActivity(), "admanager")
        }

        findViewById<AppCompatButton>(R.id.rewardedAppLovinAdButton).setOnClickListener {
            start(RewardedAdExampleActivity(), AdProvider.APPLOVIN.name.lowercase())
        }

        findViewById<AppCompatButton>(R.id.rewardedInterstitialAdButton).setOnClickListener {
            start(RewardedInterstitialAdExampleActivity(), "admob")
        }

        findViewById<AppCompatButton>(R.id.rewardedInterstitialAdManagerAdButton).setOnClickListener {
            start(RewardedInterstitialAdExampleActivity(), "admanager")
        }

        findViewById<AppCompatButton>(R.id.appOpenAdButton).setOnClickListener {
            start(AppOpenAdExampleActivity(), "admob")
        }

        findViewById<AppCompatButton>(R.id.appOpenAdManagerAdButton).setOnClickListener {
            start(AppOpenAdExampleActivity(), "admanager")
        }

        findViewById<AppCompatButton>(R.id.appOpenAppLovinAdButton).setOnClickListener {
            start(AppOpenAdExampleActivity(), AdProvider.APPLOVIN.name.lowercase())
        }

        findViewById<AppCompatButton>(R.id.bannerAdManagerAdButton).setOnClickListener {
            start(BannerAdManagerAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.bannerAppLovinAdButton).setOnClickListener {
            start(BannerApplovinAdExampleActivity())
        }

    }
}


fun AppCompatActivity.start(activity: AppCompatActivity, adProvider: String = "admob") {
    val intent = Intent(this, activity::class.java)
    with(intent) {
        putExtra("adProvider", adProvider)
        startActivity(this)
    }
}