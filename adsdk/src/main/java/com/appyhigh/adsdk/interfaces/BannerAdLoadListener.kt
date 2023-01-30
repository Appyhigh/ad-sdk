package com.appyhigh.adsdk.interfaces

abstract class BannerAdLoadListener {
    open fun onAdClicked() {}
    open fun onAdClosed(){}
    open fun onAdFailedToLoad(adError: ArrayList<String>){}
    open fun onAdImpression(){}
    open fun onAdLoaded(){}
    open fun onAdOpened(){}
}