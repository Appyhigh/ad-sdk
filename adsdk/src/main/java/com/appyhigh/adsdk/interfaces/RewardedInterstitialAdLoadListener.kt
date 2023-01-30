package com.appyhigh.adsdk.interfaces

import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

abstract class RewardedInterstitialAdLoadListener {
    open fun onAdFailedToLoad(adErrors: List<String>){}
    open fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd){}
}