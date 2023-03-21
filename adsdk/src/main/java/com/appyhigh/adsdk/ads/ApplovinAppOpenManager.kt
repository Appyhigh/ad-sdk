package com.appyhigh.adsdk.ads

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinSdk
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd

class ApplovinAppOpenManager(
    adUnitId: String,
    applicationContext: Context
) : LifecycleObserver, MaxAdListener,
    LifecycleEventObserver {
    private var appOpenAd: MaxAppOpenAd
    private var context: Context

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        context = applicationContext
        appOpenAd = MaxAppOpenAd(adUnitId, applicationContext)
        appOpenAd.setListener(this)
        appOpenAd.loadAd()
    }

    private fun showAdIfReady() {
        if (!AppLovinSdk.getInstance(context).isInitialized) return
        if (appOpenAd.isReady) {
            appOpenAd.showAd()
        } else {
            appOpenAd.loadAd()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (event == Lifecycle.Event.ON_START) {
                showAdIfReady()
            }
        }, 300)
    }


    override fun onAdLoaded(ad: MaxAd) {}
    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
    override fun onAdDisplayed(ad: MaxAd) {}
    override fun onAdClicked(ad: MaxAd) {}

    override fun onAdHidden(ad: MaxAd) {
        appOpenAd.loadAd()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        appOpenAd.loadAd()
    }
}