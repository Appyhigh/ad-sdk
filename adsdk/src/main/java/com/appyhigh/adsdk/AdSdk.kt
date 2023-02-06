package com.appyhigh.adsdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object AdSdk {
    private var isInitialized = false
    private var adConfig = AdConfig()

    fun initialize(
        application: Application,
        testDevice: String?,
        fileId: Int,
        adInitializeListener: AdInitializeListener
    ) {
        val inputStream: InputStream = try {
            application.resources.openRawResource(fileId)
        } catch (e: Exception) {
            adInitializeListener.onInitializationFailed(
                AdSdkError(
                    AdSdkErrorCode.DEFAULT_RESOURCE_NOT_FOUND,
                    application.getString(R.string.error_reading_file)
                )
            )
            null
        } ?: return
        try {
            val fileData = readDefaultAdResponseFile(inputStream)
            SharedPrefs.init(application)
            adConfig.initWithLocalFile(fileData)
        } catch (e: Exception) {
            adInitializeListener.onInitializationFailed(
                AdSdkError(
                    AdSdkErrorCode.EXCEPTION_READING_FILE,
                    application.getString(R.string.exception_reading_file)
                )
            )
            return
        }

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

    private fun readDefaultAdResponseFile(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var len: Int
        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputStream.toString().replace("\n", "")
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
        adName: String,
        contentURL: String? = null,
        neighbourContentURL: List<String>? = null
    ) {
        val fallBackId = adConfig.fetchFallbackAdUnitId(adName)
        adConfig.init()
        if (isAdActive(adName)) {
            when (adConfig.fetchAdType(adName)) {
                AdType.NATIVE -> NativeAdLoader().preloadNativeAd(
                    context,
                    adName,
                    fallBackId,
                    contentURL,
                    neighbourContentURL
                )
                AdType.BANNER -> BannerAdLoader().preloadBannerAd(
                    context,
                    adName,
                    adConfig.fetchBannerAdSize(adName),
                    fallBackId,
                    contentURL,
                    neighbourContentURL
                )
                else -> Logger.d(
                    AdSdkConstants.TAG,
                    "$adName ==== $fallBackId ==== ${context.getString(R.string.error_preloading_not_supported)}"
                )
            }
        }
    }

    fun fetchNativeAds(
        context: Context,
        parentView: ViewGroup? = null,
        adName: String,
        adsRequested: Int,
        nativeAdLoadListener: NativeAdLoadListener
    ) {
        val requestedAds = if (adsRequested < 1) {
            1
        } else if (adsRequested > 5) {
            5
        } else {
            adsRequested
        }
        loadAd(
            context = context,
            parentView = parentView,
            adName = adName,
            nativeAdLoadListener = nativeAdLoadListener,
            isNativeFetch = true,
            adsRequested = requestedAds,
            isService = false
        )
    }

    fun fetchNativeAd(
        context: Context,
        parentView: ViewGroup? = null,
        adName: String,
        nativeAdLoadListener: NativeAdLoadListener
    ) {
        loadAd(
            context = context,
            parentView = parentView,
            adName = adName,
            nativeAdLoadListener = nativeAdLoadListener,
            isNativeFetch = true,
            isService = true
        )
    }

    fun loadAd(
        context: Context,
        adName: String,
        isDarkModeEnabled: Boolean = false,
        isNativeFetch: Boolean = false,
        adsRequested: Int = 1,
        isService: Boolean = false,
        application: Application? = null,
        lifecycle: Lifecycle? = null,
        parentView: ViewGroup? = null,
        contentURL: String? = null,
        neighbourContentURL: List<String>? = null,
        bannerAdLoadListener: BannerAdLoadListener? = null,
        interstitialAdLoadListener: InterstitialAdLoadListener? = null,
        rewardedAdLoadListener: RewardedAdLoadListener? = null,
        rewardedInterstitialAdLoadListener: RewardedInterstitialAdLoadListener? = null,
        appOpenAdLoadListener: AppOpenAdLoadListener? = null,
        appOpenLoadType: AppOpenLoadType? = null,
        nativeAdLoadListener: NativeAdLoadListener? = null
    ) {
        var appOpenLoadTypeInternal = appOpenLoadType
        adConfig.init()
        val fallBackId = adConfig.fetchFallbackAdUnitId(adName)
        if (!isSdkInitialized()) {
            val error =
                "$adName ==== $fallBackId ==== ${context.getString(R.string.error_sdk_not_initialized)}"
            triggerAdFailedCallback(
                bannerAdLoadListener,
                interstitialAdLoadListener,
                rewardedAdLoadListener,
                rewardedInterstitialAdLoadListener,
                appOpenAdLoadListener,
                nativeAdLoadListener,
                error
            )
            Logger.e(AdSdkConstants.TAG, error)
            return
        }
        if (fallBackId.isBlank()) {
            val error =
                "$adName ==== $fallBackId ==== ${context.getString(R.string.error_no_fallback_id_found)}"
            triggerAdFailedCallback(
                bannerAdLoadListener,
                interstitialAdLoadListener,
                rewardedAdLoadListener,
                rewardedInterstitialAdLoadListener,
                appOpenAdLoadListener,
                nativeAdLoadListener,
                error
            )
            Logger.e(AdSdkConstants.TAG, error)
            return
        }
        if (isAdActive(adName)) {
            when (adConfig.fetchAdType(adName)) {
                AdType.NATIVE -> {
                    if (lifecycle == null && !isService) {
                        val error =
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_lifecycle)}"
                        triggerAdFailedCallback(
                            bannerAdLoadListener,
                            interstitialAdLoadListener,
                            rewardedAdLoadListener,
                            rewardedInterstitialAdLoadListener,
                            appOpenAdLoadListener,
                            nativeAdLoadListener,
                            error
                        )
                        Logger.e(AdSdkConstants.TAG, error)
                        return
                    }
                    if (parentView == null) {
                        val error =
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_parent_view)}"
                        triggerAdFailedCallback(
                            bannerAdLoadListener,
                            interstitialAdLoadListener,
                            rewardedAdLoadListener,
                            rewardedInterstitialAdLoadListener,
                            appOpenAdLoadListener,
                            nativeAdLoadListener,
                            error
                        )
                        Logger.e(AdSdkConstants.TAG, error)
                        return
                    }

                    NativeAdLoader().loadNativeAd(
                        context,
                        lifecycle,
                        parentView,
                        adName,
                        adConfig.fetchNativeAdSize(adName),
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        adConfig.fetchAdUnitRefreshTimer(adName),
                        adConfig.fetchDarkCTAColor(adName).takeIf { isDarkModeEnabled }
                            ?: adConfig.fetchLightCTAColor(adName),
                        adConfig.fetchDarkTextColor(adName).takeIf { isDarkModeEnabled }
                            ?: adConfig.fetchLightTextColor(adName),
                        (R.drawable.ad_sdk_bg_dark).takeIf { isDarkModeEnabled }
                            ?: (R.drawable.ad_sdk_bg),
                        adConfig.fetchDarkBackgroundColor(adName).takeIf { isDarkModeEnabled }
                            ?: adConfig.fetchLightBackgroundColor(adName),
                        contentURL,
                        neighbourContentURL,
                        nativeAdLoadListener,
                        false,
                        isNativeFetch,
                        adsRequested
                    )
                }
                AdType.BANNER -> {
                    if (lifecycle == null && !isService) {
                        val error =
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_lifecycle)}"
                        triggerAdFailedCallback(
                            bannerAdLoadListener,
                            interstitialAdLoadListener,
                            rewardedAdLoadListener,
                            rewardedInterstitialAdLoadListener,
                            appOpenAdLoadListener,
                            nativeAdLoadListener,
                            error
                        )
                        Logger.e(AdSdkConstants.TAG, error)
                        return
                    }
                    if (parentView == null) {
                        val error =
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_parent_view)}"
                        triggerAdFailedCallback(
                            bannerAdLoadListener,
                            interstitialAdLoadListener,
                            rewardedAdLoadListener,
                            rewardedInterstitialAdLoadListener,
                            appOpenAdLoadListener,
                            nativeAdLoadListener,
                            error
                        )
                        Logger.e(AdSdkConstants.TAG, error)
                        return
                    }
                    BannerAdLoader().loadBannerAd(
                        context,
                        lifecycle,
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
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_app_open_type)}"
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
                                "$adName ==== $fallBackId ==== ${context.getString(R.string.error_application_context_null)}"
                            triggerAdFailedCallback(
                                bannerAdLoadListener,
                                interstitialAdLoadListener,
                                rewardedAdLoadListener,
                                rewardedInterstitialAdLoadListener,
                                appOpenAdLoadListener,
                                nativeAdLoadListener,
                                error
                            )
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
                else -> Logger.d(
                    AdSdkConstants.TAG,
                    "$adName ==== $fallBackId ==== ${context.getString(R.string.unknown_ad_type)}"
                )
            }
        }
    }

    private fun triggerAdFailedCallback(
        bannerAdLoadListener: BannerAdLoadListener? = null,
        interstitialAdLoadListener: InterstitialAdLoadListener? = null,
        rewardedAdLoadListener: RewardedAdLoadListener? = null,
        rewardedInterstitialAdLoadListener: RewardedInterstitialAdLoadListener? = null,
        appOpenAdLoadListener: AppOpenAdLoadListener? = null,
        nativeAdLoadListener: NativeAdLoadListener? = null,
        error: String
    ) {
        bannerAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
        interstitialAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
        rewardedAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
        rewardedInterstitialAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
        appOpenAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
        nativeAdLoadListener?.onAdFailedToLoad(arrayListOf(error))
    }

    private fun isAdActive(adName: String): Boolean {
        return (adConfig.isAppAdsActive() && adConfig.isAdUnitActive(adName))
    }
}