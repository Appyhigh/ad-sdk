package com.appyhigh.adsdk.interfaces

import com.google.android.gms.ads.LoadAdError


abstract class NativeAdLoadListener {
    open fun onAdClicked(){}
    fun onAdClosed() {}
    fun onAdFailedToLoad(p0: LoadAdError) {}
    fun onAdImpression() {}
    fun onAdLoaded() {}
    fun onAdOpened() {}
    fun onAdSwipeGestureClicked() {}
}