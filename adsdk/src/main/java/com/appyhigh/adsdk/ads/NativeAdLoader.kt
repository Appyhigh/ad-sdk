package com.appyhigh.adsdk.ads

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.R
import com.appyhigh.adsdk.data.enums.NativeAdSize
import com.appyhigh.adsdk.interfaces.NativeAdLoadListener
import com.appyhigh.adsdk.utils.Logger
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.*

class NativeAdLoader {
    private var isAdLoaded = false
    private var adRequestsCompleted = 0
    private var adFailureReasonArray = ArrayList<String>()
    private var adUnits = ArrayList<String>()
    private var refreshCountDownTimer: CountDownTimer? = null
    private var nativeAd: NativeAd? = null

    @SuppressLint("VisibleForTests")
    fun loadNativeAd(
        context: Context,
        parentView: ViewGroup?,
        adName: String,
        adSize: NativeAdSize,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        refreshTimer: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        nativeAdLoadListener: NativeAdLoadListener?
    ) {
        isAdLoaded = false
        adRequestsCompleted = 0
        if (AdSdkConstants.adUnitsSet.contains(adName)) {
            return
        } else {
            AdSdkConstants.adUnitsSet.add(adName)
        }
        if (AdSdkConstants.preloadedNativeAdMap[adName] != null) {

        } else {
            parentView?.removeAllViews()
            adUnits.addAll(primaryAdUnitIds)
            adUnits.addAll(secondaryAdUnitIds)
            adUnits.add(fallBackId)

            val nativeShimmerBaseView =
                View.inflate(parentView?.context, R.layout.shimmer_parent_view, null)
            parentView?.addView(nativeShimmerBaseView)
            val layout = when (adSize) {
                NativeAdSize.SMALL -> R.layout.shimmer_banner_small
                NativeAdSize.MEDIUM -> R.layout.shimmer_banner_small
                NativeAdSize.BIG_V1 -> R.layout.shimmer_banner_small
                NativeAdSize.BIG_V2 -> R.layout.shimmer_banner_small
                NativeAdSize.BIG_V3 -> R.layout.shimmer_banner_small
                NativeAdSize.GRID_AD -> R.layout.shimmer_banner_small
                NativeAdSize.DEFAULT -> R.layout.shimmer_banner_small
            }
            (nativeShimmerBaseView as ShimmerFrameLayout).addView(
                View.inflate(
                    parentView?.context,
                    layout,
                    null
                )
            )
            inflateAd(
                context,
                parentView,
                adName,
                adSize,
                timeout,
                adUnits[adRequestsCompleted],
                contentURL,
                neighbourContentURL,
                nativeAdLoadListener
            )
        }

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
            nativeAdLoadListener
        )
    }

    @SuppressLint("VisibleForTests")
    private fun inflateAd(
        context: Context,
        parentView: ViewGroup?,
        adName: String,
        adSize: NativeAdSize,
        timeout: Int,
        adUnit: String,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        nativeAdLoadListener: NativeAdLoadListener?
    ) {
        val countDownTimer = object : CountDownTimer(timeout.toLong(), timeout.toLong()) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                requestNextAd(
                    context,
                    "$adUnit ==== $adName ==== Native Ad Unit Timed Out",
                    parentView,
                    adName,
                    adSize,
                    timeout,
                    contentURL,
                    neighbourContentURL,
                    nativeAdLoadListener
                )
            }
        }.start()

        val adLoader: AdLoader = AdLoader.Builder(context, adUnit)
            .forNativeAd { ad ->
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    nativeAdLoadListener?.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    nativeAdLoadListener?.onAdClosed()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    nativeAdLoadListener?.onAdFailedToLoad(p0)
                    countDownTimer?.cancel()
                    requestNextAd(
                        context,
                        "$adUnit ==== $adName ==== ${p0.message}",
                        parentView,
                        adName,
                        adSize,
                        timeout,
                        contentURL,
                        neighbourContentURL,
                        nativeAdLoadListener
                    )
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    nativeAdLoadListener?.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    nativeAd?.let { nativeAd ->
                        Logger.d(AdSdkConstants.TAG, "$adName ==== $adUnit ==== Native Ad Loaded")
                        countDownTimer?.cancel()
                        val adView =
                            View.inflate(context, R.layout.native_template_small, null)
                                    as NativeAdView
                        populateUnifiedNativeAdView(
                            nativeAd,
                            adView,
                            adSize
                        )
                        parentView?.removeAllViews()
                        parentView?.addView(adView)
                        nativeAdLoadListener?.onAdLoaded()
                    }
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    nativeAdLoadListener?.onAdOpened()
                }

                override fun onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked()
                    nativeAdLoadListener?.onAdSwipeGestureClicked()
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .setRequestCustomMuteThisAd(true)
                    .build()
            )
            .build()

        val builder = AdRequest.Builder().addNetworkExtrasBundle(
            AdMobAdapter::class.java,
            getConsentEnabledBundle()
        )
        contentURL?.let { builder.setContentUrl(it) }
        neighbourContentURL?.let { builder.setNeighboringContentUrls(it) }
        adLoader.loadAd(
            builder.build()
        )
    }

    private fun requestNextAd(
        context: Context,
        errorMsg: String,
        parentView: ViewGroup?,
        adName: String,
        adSize: NativeAdSize,
        timeout: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        nativeAdLoadListener: NativeAdLoadListener?
    ) {
        Logger.e(AdSdkConstants.TAG, errorMsg)
        adFailureReasonArray.add(errorMsg)
        adRequestsCompleted += 1
        if (adUnits.size == adRequestsCompleted) {
//            nativeAdLoadListener?.onAdFailedToLoad(adFailureReasonArray)
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
                nativeAdLoadListener
            )
        }
    }

    fun populateUnifiedNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView?,
        adType: NativeAdSize,
        textColor1: Int? = null,
        textColor2: Int? = null,
        buttonColor: String? = "#000000",
        mediaMaxHeight: Int = 300,
    ) {
        val iconView = adView?.findViewById(R.id.icon) as ImageView
        val icon = nativeAd.icon
        adView.iconView = iconView
        val iconView1 = adView.iconView
        if (iconView1 != null) {
            if (icon == null) {
                if (adType == NativeAdSize.DEFAULT) {
                    iconView1.layoutParams = LinearLayout.LayoutParams(1, mediaMaxHeight)
                }
            } else {
                if (adType == NativeAdSize.DEFAULT) {
                    iconView1.layoutParams = LinearLayout.LayoutParams(
                        mediaMaxHeight,
                        mediaMaxHeight
                    )
                }
                (iconView1 as ImageView).setImageDrawable(icon.drawable)
                iconView1.visibility = View.VISIBLE
            }
        }

        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        adView.mediaView = mediaView
        mediaView.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
        mediaView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                if (child is ImageView) { //Images
                    child.adjustViewBounds = true
                    val layoutParams1 = child.layoutParams
                    layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutParams1.height = mediaMaxHeight
                    child.layoutParams = layoutParams1
                } else { //Videos
                    val params = child.layoutParams
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
                    params.height = mediaMaxHeight
                    child.layoutParams = params
                }
            }

            override fun onChildViewRemoved(parent: View, child: View) {}
        })
        val mediaIcon = nativeAd.mediaContent
        if (mediaIcon == null || adType == NativeAdSize.MEDIUM) {
            adView.mediaView?.visibility = View.GONE
            adView.mediaView
        } else {
            adView.mediaView?.visibility = View.VISIBLE
            (adView.mediaView as MediaView).setMediaContent(mediaIcon)
        }

        val adHeadline = adView.findViewById(R.id.headline) as TextView
        adView.headlineView = adHeadline
        val headlineView = adView.headlineView
        headlineView?.visibility = View.VISIBLE
        val textView = headlineView as TextView
        textView.text = nativeAd.headline
        if (textColor1 != null) {
            textView.setTextColor(textColor1)
        }

        val adBody = adView.findViewById(R.id.body) as TextView
        adView.bodyView = adBody
        val bodyView = adView.bodyView
        if (adType == NativeAdSize.BIG_V3) {
            bodyView?.visibility = View.GONE
        } else {
            bodyView?.visibility = View.GONE
            val textView1 = bodyView as TextView
            textView1.text = nativeAd.body
            if (textColor2 != null) {
                textView1.setTextColor(textColor2)
            }
        }

        val adStore = adView.findViewById<TextView>(R.id.ad_store)
        adView.storeView = adStore
        if (nativeAd.store != null && adType == NativeAdSize.MEDIUM) {
            adView.storeView?.visibility = View.VISIBLE
            val textView1 = adView.storeView as TextView
            textView1.text = nativeAd.store
            if (textColor2 != null) {
                textView1.setTextColor(textColor2)
            }
        } else {
            adView.storeView?.visibility = View.GONE
        }


        val cta = adView.findViewById(R.id.call_to_action) as TextView
        cta.backgroundTintList = (ColorStateList.valueOf(Color.parseColor(buttonColor)))
        adView.callToActionView = cta
        adView.callToActionView?.visibility = View.VISIBLE
        (adView.callToActionView as TextView).text = nativeAd.callToAction
        adView.setNativeAd(nativeAd)
        if (nativeAd.adChoicesInfo != null && adView.adChoicesView != null) {
            try {
                val choicesView = AdChoicesView(adView.adChoicesView!!.context)
                adView.adChoicesView = choicesView
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun startRefreshTimer(
        context: Context,
        parentView: ViewGroup?,
        adName: String,
        adSize: NativeAdSize,
        fallBackId: String,
        primaryAdUnitIds: List<String>,
        secondaryAdUnitIds: List<String>,
        timeout: Int,
        refreshTimer: Int,
        contentURL: String?,
        neighbourContentURL: List<String>?,
        nativeAdLoadListener: NativeAdLoadListener?
    ) {
        refreshCountDownTimer =
            object : CountDownTimer(refreshTimer.toLong(), refreshTimer.toLong()) {
                override fun onTick(p0: Long) {}

                override fun onFinish() {
                    if (parentView?.isShown == true) {
                        AdSdkConstants.adUnitsSet.remove(adName)
                        loadNativeAd(
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
                            nativeAdLoadListener
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