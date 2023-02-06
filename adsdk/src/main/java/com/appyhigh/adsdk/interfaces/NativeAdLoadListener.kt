package com.appyhigh.adsdk.interfaces

import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd


abstract class NativeAdLoadListener {
    open fun onAdClicked() {}
    open fun onAdClosed() {}
    open fun onAdFailedToLoad(errors: List<String>) {}
    open fun onAdImpression() {}
    open fun onAdInflated() {}
    open fun onAdLoaded(nativeAd: NativeAd?) {}
    open fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {}
    open fun onAdOpened() {}
    open fun onAdSwipeGestureClicked() {}
}