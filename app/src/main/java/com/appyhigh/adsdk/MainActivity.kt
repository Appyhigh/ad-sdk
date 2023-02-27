package com.appyhigh.adsdk

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.appyhigh.adsdk.data.enums.AppOpenLoadType
import com.appyhigh.adsdk.data.enums.UpdateType
import com.appyhigh.adsdk.data.model.AdSdkError
import com.appyhigh.adsdk.interfaces.*
import com.appyhigh.adsdk.utils.Logger
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.metrics.Trace

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AdSdk.getConsentForEU(
            this,
            "1EA55AD5EAAA03034C15481D2B68CBED",
            object : ConsentRequestListener {
                override fun onError(message: String, code: Int) {
                    //Give the user a prompt or call initialize anyway
                    println("${AdSdkConstants.TAG} getConsentForEU $message $code")
                }

                override fun onSuccess() {
                    println("${AdSdkConstants.TAG} getConsentForEU SUCCESS")
                    AdSdk.initialize(
                        application = application,
                        testDevice = null,
                        fileId = R.raw.ad_utils_response,
                        adInitializeListener = object : AdInitializeListener() {
                            override fun onSdkInitialized() {
                                AdSdk.setUpVersionControl(
                                    activity = this@MainActivity,
                                    view = findViewById(R.id.tvDummyView),
                                    buildVersion = BuildConfig.VERSION_CODE,
                                    versionControlListener = object : VersionControlListener() {
                                        override fun onUpdateDetectionSuccess(updateType: UpdateType) {
                                            when (updateType) {
                                                UpdateType.SOFT_UPDATE -> {
                                                }
                                                UpdateType.HARD_UPDATE -> {
                                                }
                                                else -> {}
                                            }
                                        }
                                    }
                                )

//                    AdSdk.preloadAd(
//                        this@MainActivity,
//                        adName = "test_banner"
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
//                        adName = "test_native_ad"
//                    )
                            }

                            override fun onInitializationFailed(adSdkError: AdSdkError) {
                                Logger.e(AdSdkConstants.TAG, adSdkError.message)
                            }
                        }
                    )
                    //Call initialize method now
                    // For NON-EU countries / when a consent form is not available(i.e. if the user has already accepted the consent) this is called
                }

            })

        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, NewActivity::class.java))
//            finish()
//            AdSdk.loadAd(
//                context = this,
//                lifecycle = lifecycle,
//                parentView = findViewById(R.id.llAdView),
//                adName = "test_banner"
//            )

//            AdSdk.loadAd(
//                context = this,
//                parentView = findViewById(R.id.llAdView2),
//                adName = "test_banner_2"
////            )
//            AdSdk.loadAd(
//                context = this,
//                lifecycle = lifecycle,
//                parentView = findViewById(R.id.llAdView),
//                adName = "test_native_ad"
//            )
//            AdSdk.fetchNativeAds(
//                context = this,
//                parentView = findViewById(R.id.llAdView),
//                adName = "test_native_ad",
//                adsRequested = 5,
//                nativeAdLoadListener = object : NativeAdLoadListener() {
//
//                }
//            )
//
//            AdSdk.loadAd(
//                this,
//                parentView = findViewById(R.id.llAdView3),
//                adName = "test_banner_3",
//                fallBackId = "ca-app-pub-3940256099942544/6300978111"
//            )


//            AdSdk.loadAd(
//                context = this,
//                adName = "test_interstitial",
//                interstitialAdLoadListener = object : InterstitialAdLoadListener() {
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
//                context = this,
//                adName = "test_rewarded",
//                rewardedAdLoadListener = object :RewardedAdLoadListener(){
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
//                            }
//                        }
//                    }
//
//                }
//            )
//            AdSdk.loadAd(
//                context = this,
//                adName = "test_rewarded_interstitial",
//                rewardedInterstitialAdLoadListener = object : RewardedInterstitialAdLoadListener() {
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
            AdSdk.loadAd(
                application = application,
                context = this,
                adName = "test_app_open",
                appOpenLoadType = AppOpenLoadType.BACKGROUND_TO_FOREGROUND
            )
            AdSdk.loadAd(
                context = this,
                lifecycle = lifecycle,
                parentView = findViewById(R.id.llAdView),
                adName = "test_native_ad",
                isDarkModeEnabled = false
            )
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
        }, 2000)
    }
}