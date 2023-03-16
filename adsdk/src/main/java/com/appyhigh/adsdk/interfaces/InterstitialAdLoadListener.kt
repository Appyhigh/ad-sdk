package com.appyhigh.adsdk.interfaces

import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd

abstract class InterstitialAdLoadListener {
    open fun onAdFailedToLoad(adErrors: List<String>){}
    open fun onAdLoaded(interstitialAd: InterstitialAd){}
    open fun onApplovinAdLoaded(interstitialAd: MaxInterstitialAd){}
}