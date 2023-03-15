package com.appyhigh.adsdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appyhigh.adsdk.interfaces.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatButton>(R.id.bannerAdButton).setOnClickListener {
            start(BannerAdExampleActivity())
        }
        findViewById<AppCompatButton>(R.id.interstitialAdButton).setOnClickListener {
            start(InterstitialAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.nativeAdButton).setOnClickListener {
            start(NativeAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.rewardedAdButton).setOnClickListener {
            start(RewardedAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.rewardedInterstitialAdButton).setOnClickListener {
            start(RewardedInterstitialAdExampleActivity())
        }

        findViewById<AppCompatButton>(R.id.appOpenAdButton).setOnClickListener {
            start(AppOpenAdExampleActivity())
        }
    }
}

fun AppCompatActivity.start(activity: AppCompatActivity) {
    startActivity(Intent(this, activity::class.java))
}