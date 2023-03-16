package com.appyhigh.adsdk.interfaces

import com.applovin.mediation.ads.MaxRewardedAd
import com.google.android.gms.ads.rewarded.RewardedAd

abstract class RewardedAdLoadListener {
    open fun onAdFailedToLoad(adErrors: List<String>){}
    open fun onAdLoaded(rewardedAd: RewardedAd){}
    open fun onMaxAdLoaded(rewardedAd: MaxRewardedAd){}
}