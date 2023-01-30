package com.appyhigh.adsdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.appyhigh.adsdk.ads.*
import com.appyhigh.adsdk.data.enums.AdSdkErrorCode
import com.appyhigh.adsdk.data.enums.AdType
import com.appyhigh.adsdk.data.enums.AppOpenLoadType
import com.appyhigh.adsdk.data.local.SharedPrefs
import com.appyhigh.adsdk.data.model.AdSdkError
import com.appyhigh.adsdk.interfaces.*
import com.appyhigh.adsdk.utils.AdConfig
import com.appyhigh.adsdk.utils.Logger
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object AdSdk {
    private var isInitialized = false
    private var adConfig = AdConfig()
    fun initialize(
        application: Application,
        testDevice: String?,
        adInitializeListener: AdInitializeListener
    ) {
        if (isGooglePlayServicesAvailable(application)) {
            MobileAds.initialize(application) {
                isInitialized = true
                adInitializeListener.onSdkInitialized()
                addTestDevice(testDevice)
                Logger.d(AdSdkConstants.TAG, application.getString(R.string.sdk_successful))
                SharedPrefs.init(application)
                DynamicAds().fetchRemoteAdConfiguration(application.packageName)
            }
        } else {
            adInitializeListener.onInitializationFailed(
                AdSdkError(
                    AdSdkErrorCode.PLAY_SERVICES_NOT_FOUND,
                    application.getString(R.string.error_no_play_services)
                )
            )
        }
    }

    fun setUpVersionControl(
        activity: Activity,
        view: View,
        buildVersion: Int,
        versionControlListener: VersionControlListener?
    ) {
        SharedPrefs.init(activity)
        VersionControl().initializeVersionControl(
            activity,
            view,
            buildVersion,
            versionControlListener
        )
    }

    fun isSdkInitialized() = isInitialized

    private fun addTestDevice(testDevice: String?) {
        testDevice?.let {
            val build = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(it)).build()
            MobileAds.setRequestConfiguration(build)
        }
    }

    private fun isGooglePlayServicesAvailable(application: Application): Boolean {
        try {
            val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
            val status: Int = googleApiAvailability.isGooglePlayServicesAvailable(
                application,
                GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE
            )
            if (status != ConnectionResult.SUCCESS) {
                return false
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun preloadAd(
        context: Context,
        parentView: ViewGroup,
        adName: String,
        fallBackId: String,
        contentURL: String? = null,
        neighbourContentURL: List<String>? = null
    ) {
        adConfig.init()
        if (isAdActive(adName)) {
            when (adConfig.fetchAdType(adName)) {
                AdType.NATIVE -> {}
                AdType.BANNER -> BannerAdLoader().preloadBannerAd(
                    context,
                    adName,
                    adConfig.fetchBannerAdSize(adName),
                    fallBackId,
                    contentURL,
                    neighbourContentURL
                )
                else -> {}
            }
        }
    }

    fun loadAd(
        application: Application? = null,
        context: Context,
        parentView: ViewGroup? = null,
        adName: String,
        fallBackId: String,
        contentURL: String? = null,
        neighbourContentURL: List<String>? = null,
        bannerAdLoadListener: BannerAdLoadListener? = null,
        interstitialAdLoadListener: InterstitialAdLoadListener? = null,
        rewardedAdLoadListener: RewardedAdLoadListener? = null,
        rewardedInterstitialAdLoadListener: RewardedInterstitialAdLoadListener? = null,
        appOpenAdLoadListener: AppOpenAdLoadListener? = null,
        appOpenLoadType: AppOpenLoadType? = null
    ) {
        var appOpenLoadTypeInternal = appOpenLoadType
        adConfig.init()
        if (isAdActive(adName)) {
            when (adConfig.fetchAdType(adName)) {
                AdType.NATIVE -> {}
                AdType.BANNER -> {
                    if (parentView == null) {
                        val error = "$adName ==== $fallBackId ==== Parent View Supplied is Null"
                        bannerAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
                        Logger.e(AdSdkConstants.TAG, error)
                        return
                    }
                    BannerAdLoader().loadBannerAd(
                        context,
                        parentView,
                        adName,
                        adConfig.fetchBannerAdSize(adName),
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        adConfig.fetchAdUnitRefreshTimer(adName),
                        contentURL,
                        neighbourContentURL,
                        bannerAdLoadListener
                    )
                }
                AdType.INTERSTITIAL ->
                    InterstitialAdLoader().loadInterstitialAd(
                        context,
                        adName,
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        interstitialAdLoadListener
                    )
                AdType.REWARDED_INTERSTITIAL ->
                    RewardedInterstitialAdLoader().loadInterstitialRewardedAd(
                        context,
                        adName,
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        rewardedInterstitialAdLoadListener
                    )
                AdType.REWARDED ->
                    RewardedAdLoader().loadRewardedAd(
                        context,
                        adName,
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        rewardedAdLoadListener
                    )
                AdType.APP_OPEN -> {
                    if (appOpenLoadTypeInternal == null) {
                        val error =
                            "$adName ==== $fallBackId ==== AppOpenAdType not Supplied so switching it to AppOpenLoadType.SINGLE_LOAD"
                        Logger.e(AdSdkConstants.TAG, error)
                        appOpenLoadTypeInternal = AppOpenLoadType.SINGLE_LOAD
                    }
                    if (appOpenLoadTypeInternal == AppOpenLoadType.SINGLE_LOAD) {
                        AppOpenAdLoader().loadAppOpenAd(
                            context,
                            adName,
                            fallBackId,
                            adConfig.fetchPrimaryAdUnitIds(adName),
                            adConfig.fetchSecondaryAdUnitIds(adName),
                            adConfig.fetchAdUnitTimeout(adName),
                            appOpenAdLoadListener
                        )
                    } else {
                        if (application == null) {
                            val error =
                                "$adName ==== $fallBackId ==== Application context Supplied is Null"
                            bannerAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
                            Logger.e(AdSdkConstants.TAG, error)
                            return
                        }
                        AppOpenAdLoader().loadAppOpenAdBgToFg(
                            application,
                            context,
                            adName,
                            fallBackId,
                            adConfig.fetchPrimaryAdUnitIds(adName),
                            adConfig.fetchSecondaryAdUnitIds(adName)
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun isAdActive(adName: String): Boolean {
        return (adConfig.isAppAdsActive() && adConfig.isAdUnitActive(adName))
    }
}