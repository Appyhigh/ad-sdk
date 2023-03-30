package com.appyhigh.adsdk.interfaces

import android.widget.LinearLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAd
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd


abstract class NativeAdLoadListener {
    open fun onAdClicked() {}
    open fun onAdClosed() {}
    open fun onAdFailedToLoad(errors: List<String>) {}
    open fun onAdImpression() {}
    open fun onAdInflated() {}
    open fun onAdLoaded(nativeAd: NativeAd?) {}
    open fun onMaxAdLoaded(nativeAd: LinearLayout?) {}
    open fun onMultipleAdsLoaded(nativeAds: ArrayList<NativeAd?>) {}
    open fun onAdOpened() {}
    open fun onAdSwipeGestureClicked() {}
}