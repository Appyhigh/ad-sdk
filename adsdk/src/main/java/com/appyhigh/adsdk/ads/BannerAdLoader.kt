package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.AdSdkConstants.consentDisabledBundle
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.data.enums.AdProvider
import com.appyhigh.adsdk.interfaces.BannerAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest

internal class BannerAdLoader {
    private var isAdLoaded = false
    private var adRequestsCompleted = 0
    private var adFailureReasonArray = ArrayList<String>()
    private var adUnits = ArrayList<String>()
    private var adUnitsProvider = ArrayList<String>()
    private var refreshCountDownTimer: CountDownTimer? = null

    @SuppressLint("VisibleForTests")
    fun preloadBannerAd(
        context: Context,
        adName: String,
        adSize: AdSize,
        adUnitId: String,
        adProvider: String,
        contentURL: String?,
        neighbourContentURL: List<String>?
    ) {
        if (AdSdkConstants.preloadedBannerAdMap[adName] == null) {
            if (adProvider == AdProvider.APPLOVIN.name.lowercase()) {
                val mAdView = MaxAdView(adUnitId, context)
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val heightPx = context.resources.getDimensionPixelSize(R.dimen.banner_height)
                mAdView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
                mAdView.setBackgroundColor(Color.WHITE)
                mAdView.loadAd()
                mAdView.setListener(object : MaxAdViewAdListener {
                    override fun onAdLoaded(p0: MaxAd?) {
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnitId ==== ${context.getString(R.string.banner_preloaded)}"
                        )
                        AdSdkConstants.preloadedBannerAdMap[adName] = mAdView
                    }

                    override fun onAdDisplayed(p0: MaxAd?) {
                    }

                    override fun onAdHidden(p0: MaxAd?) {
                    }

                    override fun onAdClicked(p0: MaxAd?) {
                    }

                    override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                        Logger.e(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnitId ==== ${context.getString(R.string.error_preloading_banner_failed)}"
                        )
                    }

                    override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                    }

                    override fun onAdExpanded(p0: MaxAd?) {
                    }

                    override fun onAdCollapsed(p0: MaxAd?) {
                    }
                })
            } else {
                val builder = if (adProvider == AdProvider.ADMOB.name.lowercase()) {
                    AdRequest.Builder()
                } else {
                    AdManagerAdRequest.Builder()
                }
                builder.addNetworkExtrasBundle(
                    AdMobAdapter::class.java,
                    if (AdSdkConstants.consentStatus) consentDisabledBundle else bundleOf()
                )
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
                            "$adName ==== $adUnitId ==== ${context.getString(R.string.error_preloading_banner_failed)}"
                        )
                    }

                    override fun onAdImpression() {}
                    override fun onAdLoaded() {
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnitId ==== ${context.getString(R.string.banner_preloaded)}"
                        )
                        AdSdkConstants.preloadedBannerAdMap[adName] = mAdView
                    }

                    override fun onAdOpened() {}
                }
            }

        } else {
            return
        }
    }

    @SuppressLint("VisibleForTests")
    fun loadBannerAd(
        context: Context,
        lifecycle: Lifecycle?,
        parentView: ViewGroup,
        adName: String,
        adSize: AdSize,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        primaryAdUnitProvider: String,
        secondaryAdUnitProvider: String,
        timeout: Int,
        refreshTimer: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?,
        isLocalRefresh: Boolean = false,
        showShimmerLoading: Boolean = true
    ) {

        if (!isLocalRefresh) {
            lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(
                    source: LifecycleOwner,
                    event: Lifecycle.Event
                ) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        cancelRefreshTimer(adName, fallBackId, parentView.context)
                    }
                }
            })
        }

        isAdLoaded = false
        adRequestsCompleted = 0
        if (AdSdkConstants.adUnitsSet.contains(adName + parentView.toString())) {
            return
        } else {
            AdSdkConstants.adUnitsSet.add(adName + parentView.toString())
        }
        if (AdSdkConstants.preloadedBannerAdMap[adName] != null) {
            Logger.d(
                AdSdkConstants.TAG,
                "$adName ==== $fallBackId ==== ${context.getString(R.string.preloaded_banner_displayed)}"
            )
            parentView.removeAllViews()
            if (AdSdkConstants.preloadedBannerAdMap[adName] is MaxAdView) {
                parentView.addView(AdSdkConstants.preloadedBannerAdMap[adName] as MaxAdView)
            } else {
                parentView.addView(AdSdkConstants.preloadedBannerAdMap[adName] as AdView)
            }
            setAdViewListener(
                context,
                lifecycle,
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
            AdSdkConstants.preloadedBannerAdMap[adName] = null

        } else {
            for (adUnit in primaryAdUnitIds) {
                adUnits.add(adUnit)
                adUnitsProvider.add(primaryAdUnitProvider)
            }

            for (adUnit in secondaryAdUnitIds) {
                adUnits.add(adUnit)
                adUnitsProvider.add(secondaryAdUnitProvider)
            }
            adUnits.add(fallBackId)
            adUnitsProvider.add(AdProvider.ADMOB.name.lowercase())

            if (!isLocalRefresh && showShimmerLoading) {
                val bannerShimmerBaseView =
                    View.inflate(parentView.context, R.layout.shimmer_parent_view, null)
                parentView.addView(bannerShimmerBaseView)
                val layout = when (adSize) {
                    AdSize.BANNER -> R.layout.shimmer_banner_small
                    AdSize.LARGE_BANNER -> R.layout.shimmer_banner_large
                    AdSize.MEDIUM_RECTANGLE -> R.layout.shimmer_banner_medium_rectangle
                    else -> R.layout.shimmer_banner_small
                }
                (bannerShimmerBaseView as ShimmerFrameLayout).addView(
                    View.inflate(
                        parentView.context,
                        layout,
                        null
                    )
                )
            }
            inflateAd(
                context,
                lifecycle,
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
        startRefreshTimer(
            context,
            lifecycle,
            parentView,
            adName,
            adSize,
            fallBackId,
            primaryAdUnitIds,
            secondaryAdUnitIds,
            primaryAdUnitProvider,
            secondaryAdUnitProvider,
            timeout,
            refreshTimer,
            contentURL,
            neighbourContentURL,
            bannerAdLoadListener
        )
    }

    @SuppressLint("VisibleForTests")
    private fun inflateAd(
        context: Context,
        lifecycle: Lifecycle?,
        parentView: ViewGroup,
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
                    lifecycle,
                    "$adUnit ==== $adName ==== ${context.getString(R.string.error_banner_timed_out)}",
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
        val mAdView: ViewGroup
        if (adUnitsProvider[adRequestsCompleted] == AdProvider.APPLOVIN.name.lowercase()) {
            mAdView = MaxAdView(adUnit, context)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val heightPx = context.resources.getDimensionPixelSize(R.dimen.banner_height)
            mAdView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
            mAdView.setBackgroundColor(Color.WHITE)
            parentView.removeAllViews()
            parentView.addView(mAdView)
            mAdView.loadAd()
        } else {
            val builder =
                if (adUnitsProvider[adRequestsCompleted] == AdProvider.ADMOB.name.lowercase()) {
                    AdRequest.Builder()
                } else {
                    AdManagerAdRequest.Builder()
                }
            builder.addNetworkExtrasBundle(
                AdMobAdapter::class.java,
                if (!AdSdkConstants.consentStatus) consentDisabledBundle else bundleOf()
            )
            contentURL?.let { builder.setContentUrl(it) }
            neighbourContentURL?.let { builder.setNeighboringContentUrls(it) }
            val adRequest = builder.build()
            mAdView = AdView(context)
            mAdView.setAdSize(adSize)
            mAdView.adUnitId = adUnit
            mAdView.loadAd(adRequest)
        }
        setAdViewListener(
            context,
            lifecycle,
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
        lifecycle: Lifecycle?,
        errorMsg: String,
        parentView: ViewGroup,
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
            parentView.removeAllViews()
        } else {
            if (adUnits.size > adRequestsCompleted) {
                inflateAd(
                    context,
                    lifecycle,
                    parentView,
                    adName,
                    adSize,
                    timeout,
                    adUnits[adRequestsCompleted],
                    contentURL,
                    neighbourContentURL,
                    bannerAdLoadListener
                )
            } else {
                bannerAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
                parentView.removeAllViews()
            }
        }
    }

    private fun setAdViewListener(
        context: Context,
        lifecycle: Lifecycle?,
        mAdView: Any,
        countDownTimer: CountDownTimer?,
        parentView: ViewGroup,
        adName: String,
        adSize: AdSize,
        timeout: Int,
        adUnit: String,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        bannerAdLoadListener: BannerAdLoadListener?,
    ) {
        if (mAdView is AdView) {
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
                        lifecycle,
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
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ==== $adUnit ==== ${context.getString(R.string.banner_loaded)}"
                        )
                        bannerAdLoadListener?.onAdLoaded()
                        countDownTimer?.cancel()
                        isAdLoaded = true
                        parentView.removeAllViews()
                        parentView.addView(mAdView)
                    }
                }

                override fun onAdOpened() {
                    bannerAdLoadListener?.onAdOpened()
                }
            }
        } else if (mAdView is MaxAdView) {
            mAdView.setListener(
                object : MaxAdViewAdListener {
                    override fun onAdLoaded(ad: MaxAd) {
                        if (!isAdLoaded) {
                            Logger.d(
                                AdSdkConstants.TAG,
                                "$adName ==== $adUnit ==== ${context.getString(R.string.banner_loaded)}"
                            )
                            bannerAdLoadListener?.onAdLoaded()
                            countDownTimer?.cancel()
                            isAdLoaded = true
                            parentView.removeAllViews()
                            parentView.addView(mAdView)
                        }
                    }

                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        countDownTimer?.cancel()
                        requestNextAd(
                            context,
                            lifecycle,
                            adUnit + " ==== " + adName + " ==== " + error.message,
                            parentView,
                            adName,
                            adSize,
                            timeout,
                            contentURL,
                            neighbourContentURL,
                            bannerAdLoadListener
                        )
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                        countDownTimer?.cancel()
                        requestNextAd(
                            context,
                            lifecycle,
                            adUnit + " ==== " + adName + " ==== " + error.message,
                            parentView,
                            adName,
                            adSize,
                            timeout,
                            contentURL,
                            neighbourContentURL,
                            bannerAdLoadListener
                        )
                    }

                    override fun onAdDisplayed(ad: MaxAd) {
                        bannerAdLoadListener?.onAdImpression()
                    }

                    override fun onAdClicked(ad: MaxAd) {
                        bannerAdLoadListener?.onAdClicked()
                    }

                    override fun onAdHidden(ad: MaxAd) {
                        bannerAdLoadListener?.onAdClosed()
                    }

                    override fun onAdExpanded(ad: MaxAd) {
                        bannerAdLoadListener?.onAdOpened()
                    }

                    override fun onAdCollapsed(ad: MaxAd) {
                        bannerAdLoadListener?.onAdClosed()
                    }
                }
            )
        }
    }

    private fun startRefreshTimer(
        context: Context,
        lifecycle: Lifecycle?,
        parentView: ViewGroup,
        adName: String,
        adSize: AdSize,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        primaryAdUnitProvider: String,
        secondaryAdUnitProvider: String,
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
                    if (parentView.isShown) {
                        AdSdkConstants.adUnitsSet.remove(adName + parentView.toString())
                        loadBannerAd(
                            context,
                            lifecycle,
                            parentView,
                            adName,
                            adSize,
                            fallBackId,
                            primaryAdUnitIds,
                            secondaryAdUnitIds,
                            primaryAdUnitProvider,
                            secondaryAdUnitProvider,
                            timeout,
                            refreshTimer,
                            contentURL,
                            neighbourContentURL,
                            bannerAdLoadListener,
                            true
                        )
                    } else {
                        Logger.d(
                            AdSdkConstants.TAG,
                            "$adName ${context.getString(R.string.error_refresh_cancelled)}"
                        )
                        refreshCountDownTimer?.start()
                    }
                }
            }.start()
    }

    private fun cancelRefreshTimer(
        adName: String,
        fallBackId: String,
        context: Context
    ) {
        refreshCountDownTimer?.cancel()
        Logger.d(
            AdSdkConstants.TAG,
            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_refresh_cancelled_parent_view)}"
        )
    }

}