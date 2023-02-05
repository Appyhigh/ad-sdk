package com.appyhigh.adsdk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.appyhigh.adsdk.data.enums.AppOpenLoadType
import com.appyhigh.adsdk.data.enums.UpdateType
import com.appyhigh.adsdk.data.model.AdSdkError
import com.appyhigh.adsdk.interfaces.*
import com.appyhigh.adsdk.utils.Logger
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.nativead.NativeAd

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AdSdk.initialize(
            application,
            null,
            R.raw.ad_utils_response,
            object : AdInitializeListener() {
                override fun onSdkInitialized() {
                    AdSdk.setUpVersionControl(
                        this@MainActivity,
                        findViewById(R.id.tvDummyView),
                        BuildConfig.VERSION_CODE,
                        object : VersionControlListener() {
                            override fun onUpdateDetectionSuccess(updateType: UpdateType) {
                                when (updateType) {
                                    UpdateType.SOFT_UPDATE -> {
                                        Logger.d(AdSdkConstants.TAG, UpdateType.SOFT_UPDATE.name)
                                    }
                                    UpdateType.HARD_UPDATE -> {
                                        Logger.d(AdSdkConstants.TAG, UpdateType.HARD_UPDATE.name)
                                    }
                                    else -> {
                                        Logger.d(AdSdkConstants.TAG, UpdateType.NO_UPDATE.name)
                                    }
                                }
                            }
                        }
                    )

//                    AdSdk.preloadAd(
//                        this@MainActivity,
//                        parentView = findViewById(R.id.llAdView),
//                        adName = "test_banner",
//                        fallBackId = "ca-app-pub-3940256099942544/6300978111"
//                    )
//
//                    AdSdk.preloadAd(
//                        this@MainActivity,
//                        parentView = findViewById(R.id.llAdView2),
//                        adName = "test_banner_2",
//                        fallBackId = "ca-app-pub-3940256099942544/6300978111"
//                    )
//
//                    AdSdk.preloadAd(
//                        this@MainActivity,
//                        parentView = findViewById(R.id.llAdView3),
//                        adName = "test_banner_3",
//                        fallBackId = "ca-app-pub-3940256099942544/6300978111"
//                    )
//                    AdSdk.preloadAd(
//                        context = this@MainActivity,
//                        adName = "test_native_ad",
//                        fallBackId = "ca-app-pub-3940256099942544/2247696110"
//                    )
                }

                override fun onInitializationFailed(adSdkError: AdSdkError) {
                    Logger.e(AdSdkConstants.TAG, adSdkError.message)
                }
            }
        )

        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, NewActivity::class.java))
//            finish()
            AdSdk.loadAd(
                this,
                parentView = findViewById(R.id.llAdView),
                adName = "test_banner"
            )
//
//            AdSdk.loadAd(
//                this,
//                parentView = findViewById(R.id.llAdView2),
//                adName = "test_banner_2",
//                fallBackId = "ca-app-pub-3940256099942544/6300978111"
//            )
//
//            AdSdk.loadAd(
//                this,
//                parentView = findViewById(R.id.llAdView3),
//                adName = "test_banner_3",
//                fallBackId = "ca-app-pub-3940256099942544/6300978111"
//            )

//            AdSdk.loadAd(
//                this,
//                adName = "test_interstitial",
//                fallBackId = "ca-app-pub-3940256099942544/1033173712",
//                interstitialAdLoadListener = object : InterstitialAdLoadListener {
//                    override fun onAdFailedToLoad(adErrors: List<String>) {
//                        for (error in adErrors) {
//                            Logger.e(AdSdkConstants.TAG, error)
//                        }
//                    }
//
//                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                        interstitialAd.show(this@MainActivity)
//                    }
//                }
//            )

//            AdSdk.loadAd(
//                this,
//                adName = "test_rewarded",
//                fallBackId = "ca-app-pub-3940256099942544/5224354917",
//                rewardedAdLoadListener = object :RewardedAdLoadListener{
//                    override fun onAdFailedToLoad(adErrors: List<String>) {
//                        for (error in adErrors) {
//                            Logger.e(AdSdkConstants.TAG, error)
//                        }
//                    }
//
//                    override fun onAdLoaded(rewardedAd: RewardedAd) {
//                        rewardedAd.show(this@MainActivity) {
//                            fun onUserEarnedReward(rewardItem: RewardItem) {
//                                var rewardAmount = rewardItem.amount
//                                var rewardType = rewardItem.type
//                                Logger.d(AdSdkConstants.TAG, "User earned the reward.")
//                            }
//                        }
//                    }
//
//                }
//            )
//            AdSdk.loadAd(
//                context = this,
//                adName = "test_rewarded_interstitial",
//                fallBackId = "ca-app-pub-3940256099942544/5354046379",
//                rewardedInterstitialAdLoadListener = object : RewardedInterstitialAdLoadListener {
//                    override fun onAdFailedToLoad(adErrors: List<String>) {
//                        for (error in adErrors) {
//                            Logger.e(AdSdkConstants.TAG, error)
//                        }
//                    }
//
//                    override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
//                        rewardedInterstitialAd.show(this@MainActivity) {
//                            fun onUserEarnedReward(rewardItem: RewardItem) {
//                                var rewardAmount = rewardItem.amount
//                                var rewardType = rewardItem.type
//                                Logger.d(AdSdkConstants.TAG, "User earned the reward.")
//                            }
//                        }
//                    }
//                }
//            )
//            AdSdk.loadAd(
//                application = application,
//                context = this,
//                adName = "test_app_open",
//                fallBackId = "ca-app-pub-3940256099942544/3419835294",
//                appOpenLoadType = AppOpenLoadType.BACKGROUND_TO_FOREGROUND,
//                appOpenAdLoadListener = object : AppOpenAdLoadListener() {
//                    override fun onAdLoaded(ad: AppOpenAd) {
//                        super.onAdLoaded(ad)
//                        ad.show(this@MainActivity)
//                    }
//                }
//            )
//            AdSdk.loadAd(
//                context = this,
//                lifecycle = lifecycle,
//                parentView = findViewById(R.id.llAdView),
//                adName = "test_native_ad",
//                fallBackId = "ca-app-pub-3940256099942544/2247696110",
//            )
//            if (AdSdk.isSdkInitialized())
//                AdSdk.fetchNativeAds(
//                    context = this,
//                    parentView = findViewById(R.id.llAdView3),
//                    adName = "test_native_ad",
//                    fallBackId = "ca-app-pub-3940256099942544/2247696110",
//                    adsRequested = 5,
//                    nativeAdLoadListener = object : NativeAdLoadListener() {
//                        override fun onAdLoaded(nativeAd: NativeAd?) {
//                            super.onAdLoaded(nativeAd)
//                            Logger.d(AdSdkConstants.TAG, nativeAd.toString())
//                        }
//
//                        override fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {
//                            super.onMultipleAdsLoaded(nativeAds)
//                            for (ad in nativeAds) {
//                                Logger.d(AdSdkConstants.TAG, ad.toString())
//                            }
//                        }
//                    }
//                )
        }, 5000)
    }
}