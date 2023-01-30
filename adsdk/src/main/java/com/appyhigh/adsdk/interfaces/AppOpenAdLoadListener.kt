package com.appyhigh.adsdk.interfaces

import com.appyhigh.adsdk.ads.AppOpenAdManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

abstract class AppOpenAdLoadListener {
    open fun onInitSuccess(manager: AppOpenAdManager?) {}
    open fun onAdLoaded(ad: AppOpenAd) {}
    open fun onAdFailedToLoad(loadAdError: List<String>) {}
    open fun onAdFailedToShow(adError: AdError) {}
    open fun onAdClosed() {}
    open fun onContextFailed() {}
}

abstract class AppOpenAdLoadListenerInternal {
    open fun onAdLoaded(ad: AppOpenAd) {}
    open fun onAdFailedToLoad(loadAdError: LoadAdError) {}
}