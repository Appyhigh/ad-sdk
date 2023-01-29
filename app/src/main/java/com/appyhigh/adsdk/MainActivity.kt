package com.appyhigh.adsdk

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.appyhigh.adsdk.data.enums.UpdateType
import com.appyhigh.adsdk.data.model.AdSdkError
import com.appyhigh.adsdk.interfaces.AdInitializeListener
import com.appyhigh.adsdk.interfaces.VersionControlListener
import com.appyhigh.adsdk.utils.Logger

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AdSdk.initialize(
            application,
            null,
            object : AdInitializeListener {
                override fun onSdkInitialized() {
                    AdSdk.setUpVersionControl(
                        this@MainActivity,
                        findViewById(R.id.tvDummyView),
                        BuildConfig.VERSION_CODE,
                        object : VersionControlListener {
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

                    AdSdk.preloadAd(
                        parentView = findViewById(R.id.llAdView),
                        adName = "test_banner",
                        fallBackId = "ca-app-pub-3940256099942544/6300978111"
                    )

                    AdSdk.preloadAd(
                        parentView = findViewById(R.id.llAdView2),
                        adName = "test_banner_2",
                        fallBackId = "ca-app-pub-3940256099942544/6300978111"
                    )

                    AdSdk.preloadAd(
                        parentView = findViewById(R.id.llAdView3),
                        adName = "test_banner_3",
                        fallBackId = "ca-app-pub-3940256099942544/6300978111"
                    )
                }

                override fun onInitializationFailed(adSdkError: AdSdkError) {
                    Logger.e("AdSdkError", adSdkError.message)
                }
            }
        )

        Handler(Looper.getMainLooper()).postDelayed({
            AdSdk.loadAd(
                parentView = findViewById(R.id.llAdView),
                adName = "test_banner",
                fallBackId = "ca-app-pub-3940256099942544/6300978111"
            )

            AdSdk.loadAd(
                parentView = findViewById(R.id.llAdView2),
                adName = "test_banner_2",
                fallBackId = "ca-app-pub-3940256099942544/6300978111"
            )

            AdSdk.loadAd(
                parentView = findViewById(R.id.llAdView3),
                adName = "test_banner_3",
                fallBackId = "ca-app-pub-3940256099942544/6300978111"
            )
        }, 8000)
    }
}