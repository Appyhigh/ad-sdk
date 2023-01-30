package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.interfaces.BannerAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*

class BannerAdLoader {
    private var isAdLoaded = false
    private var adRequestsCompleted = 0
    private var adFailureReasonArray = ArrayList<String>()
    private var adUnits = ArrayList<String>()
    private var refreshCountDownTimer: CountDownTimer? = null

    @SuppressLint("VisibleForTests")
    fun preloadBannerAd(
        context: Context,
        adName: String,
        adSize: AdSize,
        adUnitId: String,
        contentURL: String?,
        neighbourContentURL: List<String>?
    ) {
        if (AdSdkConstants.preloadedBannerAdMap[adName] == null) {
            val builder = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, getConsentEnabledBundle())
            contentURL?.let { builder.setContentUrl(it) }
            neighbourContentURL?.let { builder.setNeighboringContentUrls(it) }
            val adRequest = builder.build()
            val mAdView = AdView(context)
            mAdView.setAdSize(adSize)
            mAdView.adUnitId = adUnitId
            mAdView.loadAd(adRequest)
            mAdView.adListener = object : AdListener() {
                override fun onAdClicked() {}
                override fun onAdClosed() {}
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Logger.e(
                        AdSdkConstants.TAG,
                        "$adName ==== $adUnitId ==== Preloading Banner Ad Failed"
                    )
                }

                override fun onAdImpression() {}
                override fun onAdLoaded() {
                    Logger.d(AdSdkConstants.TAG, "$adName ==== $adUnitId ==== Preloaded Banner Ad")
                    AdSdkConstants.preloadedBannerAdMap[adName] = mAdView
                }

                override fun onAdOpened() {}
            }

        } else {
            return
        }
    }

    @SuppressLint("VisibleForTests")
    fun loadBannerAd(
        context: Context,
        parentView: ViewGroup?,
        adName: String,
        adSize: AdSize,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        refreshTimer: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?
    ) {
        isAdLoaded = false
        adRequestsCompleted = 0
        if (AdSdkConstants.adUnitsSet.contains(adName)) {
            return
        } else {
            AdSdkConstants.adUnitsSet.add(adName)
        }
        if (AdSdkConstants.preloadedBannerAdMap[adName] != null) {
            Logger.d(
                AdSdkConstants.TAG,
                "$adName ==== $fallBackId ==== Preloaded Banner Ad Displayed"
            )
            parentView?.removeAllViews()
            parentView?.addView(AdSdkConstants.preloadedBannerAdMap[adName])
            setAdViewListener(
                context,
                AdSdkConstants.preloadedBannerAdMap[adName]!!,
                null,
                parentView,
                adName,
                adSize,
                timeout,
                fallBackId,
                contentURL,
                neighbourContentURL,
                bannerAdLoadListener
            )
            startRefreshTimer(
                context,
                parentView,
                adName,
                adSize,
                fallBackId,
                primaryAdUnitIds,
                secondaryAdUnitIds,
                timeout,
                refreshTimer,
                contentURL,
                neighbourContentURL,
                bannerAdLoadListener
            )
            AdSdkConstants.preloadedBannerAdMap[adName] = null

        } else {
            parentView?.removeAllViews()
            adUnits.addAll(primaryAdUnitIds)
            adUnits.addAll(secondaryAdUnitIds)
            adUnits.add(fallBackId)
            val bannerShimmerBaseView =
                View.inflate(parentView?.context, R.layout.banner_shimmer, null)
            parentView?.addView(bannerShimmerBaseView)
            when (adSize) {
                AdSize.BANNER -> {
                    val bannerShimmerSmall =
                        View.inflate(parentView?.context, R.layout.shimmer_banner_small, null)
                    (bannerShimmerBaseView as ShimmerFrameLayout).addView(bannerShimmerSmall)
                }
                AdSize.LARGE_BANNER -> {
                    val bannerShimmerSmall =
                        View.inflate(parentView?.context, R.layout.shimmer_banner_large, null)
                    (bannerShimmerBaseView as ShimmerFrameLayout).addView(bannerShimmerSmall)
                }
                AdSize.MEDIUM_RECTANGLE -> {
                    val bannerShimmerSmall =
                        View.inflate(
                            parentView?.context,
                            R.layout.shimmer_banner_medium_rectangle,
                            null
                        )
                    (bannerShimmerBaseView as ShimmerFrameLayout).addView(bannerShimmerSmall)
                }
            }
            inflateAd(
                context,
                parentView,
                adName,
                adSize,
                timeout,
                adUnits[adRequestsCompleted],
                contentURL,
                neighbourContentURL,
                bannerAdLoadListener
            )
            startRefreshTimer(
                context,
                parentView,
                adName,
                adSize,
                fallBackId,
                primaryAdUnitIds,
                secondaryAdUnitIds,
                timeout,
                refreshTimer,
                contentURL,
                neighbourContentURL,
                bannerAdLoadListener
            )
        }
    }

    @SuppressLint("VisibleForTests")
    private fun inflateAd(
        context: Context,
        parentView: ViewGroup?,
        adName: String,
        adSize: AdSize,
        timeout: Int,
        adUnit: String,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?
    ) {
        val countDownTimer = object : CountDownTimer(timeout.toLong(), timeout.toLong()) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                requestNextAd(
                    context,
                    "$adUnit ==== $adName ==== Banner Ad Unit Timed Out",
                    parentView,
                    adName,
                    adSize,
                    timeout,
                    contentURL,
                    neighbourContentURL,
                    bannerAdLoadListener
                )
            }
        }.start()
        val builder = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, getConsentEnabledBundle())
        contentURL?.let { builder.setContentUrl(it) }
        neighbourContentURL?.let { builder.setNeighboringContentUrls(it) }
        val adRequest = builder.build()
        val mAdView = AdView(context)
        mAdView.setAdSize(adSize)
        mAdView.adUnitId = adUnit
        mAdView.loadAd(adRequest)
        setAdViewListener(
            context,
            mAdView,
            countDownTimer,
            parentView,
            adName,
            adSize,
            timeout,
            adUnit,
            contentURL,
            neighbourContentURL,
            bannerAdLoadListener
        )
    }

    private fun requestNextAd(
        context: Context,
        errorMsg: String,
        parentView: ViewGroup?,
        adName: String,
        adSize: AdSize,
        timeout: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?
    ) {
        Logger.e(AdSdkConstants.TAG, errorMsg)
        adFailureReasonArray.add(errorMsg)
        adRequestsCompleted += 1
        if (adUnits.size == adRequestsCompleted) {
            bannerAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
            parentView?.removeAllViews()
        } else {
            inflateAd(
                context,
                parentView,
                adName,
                adSize,
                timeout,
                adUnits[adRequestsCompleted],
                contentURL,
                neighbourContentURL,
                bannerAdLoadListener
            )
        }
    }

    private fun setAdViewListener(
        context: Context,
        mAdView: AdView,
        countDownTimer: CountDownTimer?,
        parentView: ViewGroup?,
        adName: String,
        adSize: AdSize,
        timeout: Int,
        adUnit: String,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?,
    ) {
        mAdView.adListener = object : AdListener() {
            override fun onAdClicked() {
                bannerAdLoadListener?.onAdClicked()
            }

            override fun onAdClosed() {
                bannerAdLoadListener?.onAdClosed()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                countDownTimer?.cancel()
                requestNextAd(
                    context,
                    adUnit + " ==== " + adName + " ==== " + adError.message,
                    parentView,
                    adName,
                    adSize,
                    timeout,
                    contentURL,
                    neighbourContentURL,
                    bannerAdLoadListener
                )
            }

            override fun onAdImpression() {
                bannerAdLoadListener?.onAdImpression()
            }

            override fun onAdLoaded() {
                if (!isAdLoaded) {
                    Logger.d(AdSdkConstants.TAG, "$adName ==== $adUnit ==== Banner Ad Loaded")
                    bannerAdLoadListener?.onAdLoaded()
                    countDownTimer?.cancel()
                    isAdLoaded = true
                    parentView?.removeAllViews()
                    parentView?.addView(mAdView)
                }
            }

            override fun onAdOpened() {
                bannerAdLoadListener?.onAdOpened()
            }
        }
    }

    private fun startRefreshTimer(
        context: Context,
        parentView: ViewGroup?,
        adName: String,
        adSize: AdSize,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        refreshTimer: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?
    ) {
        refreshCountDownTimer =
            object : CountDownTimer(refreshTimer.toLong(), refreshTimer.toLong()) {
                override fun onTick(p0: Long) {}

                override fun onFinish() {
                    if (parentView?.isShown == true) {
                        AdSdkConstants.adUnitsSet.remove(adName)
                        loadBannerAd(
                            context,
                            parentView,
                            adName,
                            adSize,
                            fallBackId,
                            primaryAdUnitIds,
                            secondaryAdUnitIds,
                            timeout,
                            refreshTimer,
                            contentURL,
                            neighbourContentURL,
                            bannerAdLoadListener
                        )
                    } else {
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName refresh cancelled as view is not visible"
                        )
                        refreshCountDownTimer?.start()
                    }
                }
            }.start()
    }

    private val extras = Bundle()
    private fun getConsentEnabledBundle(): Bundle {
        return extras
    }
}