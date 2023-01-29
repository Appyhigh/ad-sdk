package com.appyhigh.adsdk.interfaces

interface BannerAdLoadListener {
    fun onAdClicked()
    fun onAdClosed()
    fun onAdFailedToLoad(adError : ArrayList<String>)
    fun onAdImpression()
    fun onAdLoaded()
    fun onAdOpened()
}