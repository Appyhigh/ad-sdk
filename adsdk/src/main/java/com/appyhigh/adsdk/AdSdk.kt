package com.appyhigh.adsdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import com.applovin.sdk.AppLovinSdk
import com.appyhigh.adsdk.ads.*
import com.appyhigh.adsdk.data.enums.AdProvider
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
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object AdSdk {
    private var isInitialized = false
    private var adConfig = AdConfig()
    private var isAppOpenAlreadyRegistered = false
    private var isAdMobInitialized = false
    private var isAppLovinInitialized = false
    fun getConsentForEU(
        activity: Activity,
        testDeviceHashedId: String? = null,
        consentRequestListener: ConsentRequestListener
    ) {
        try {
            val params = testDeviceHashedId?.let {
                val debugSettings = ConsentDebugSettings.Builder(activity)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(it)
                    .build()

                ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .setConsentDebugSettings(debugSettings)
                    .build()
            } ?: run {
                ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .build()
            }

            val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                {
                    if (consentInformation.isConsentFormAvailable) {
                        loadForm(consentInformation, activity, consentRequestListener)
                    } else {
                        AdSdkConstants.consentStatus = true
                        consentRequestListener.onSuccess()
                    }
                },
                {
                    AdSdkConstants.consentStatus = false
                    consentRequestListener.onError(it.message, it.errorCode)
                })
        } catch (e: Exception) {
            AdSdkConstants.consentStatus = false
            consentRequestListener.onError(
                e.message ?: activity.getString(R.string.consent_exception), 1
            )
        }
    }

    private fun loadForm(
        consentInformation: ConsentInformation,
        activity: Activity,
        consentRequestListener: ConsentRequestListener
    ) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm ->
                when (consentInformation.consentStatus) {
                    ConsentInformation.ConsentStatus.REQUIRED -> {
                        consentForm.show(
                            activity,
                        ) {
                            loadForm(consentInformation, activity, consentRequestListener)
                        }
                    }
                    else -> {
                        AdSdkConstants.consentStatus = true
                        consentRequestListener.onSuccess()
                    }
                }
            },
            {
                AdSdkConstants.consentStatus = false
                consentRequestListener.onError(it.message, it.errorCode)
            }
        )
    }

    fun initialize(
        application: Application,
        testDevice: String?,
        advertisingId: String?,
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
            addTestDevice(testDevice, advertisingId, application)
            SharedPrefs.init(application)
            DynamicAds().fetchRemoteAdConfiguration(application.packageName)
            MobileAds.initialize(application) {
                Logger.d(AdSdkConstants.TAG, "admob")
                isAdMobInitialized = true
                areBothSdksInitialized(application, adInitializeListener)
            }
            AppLovinSdk.getInstance(application).initializeSdk {
                Logger.d(AdSdkConstants.TAG, "applovin")
                isAppLovinInitialized = true
                areBothSdksInitialized(application, adInitializeListener)
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

    private fun areBothSdksInitialized(application: Application, adInitializeListener: AdInitializeListener){
        if (isAdMobInitialized && isAppLovinInitialized){
            AppLovinSdk.getInstance(application).mediationProvider = "max"
            isInitialized = true
            Logger.d(AdSdkConstants.TAG, application.getString(R.string.sdk_callback))
            adInitializeListener.onSdkInitialized()
            Logger.d(AdSdkConstants.TAG, application.getString(R.string.sdk_successful))
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

    private fun addTestDevice(
        testDevice: String?,
        advertisingId: String?,
        application: Application
    ) {
        if (BuildConfig.DEBUG) {
            AppLovinSdk.getInstance(application).settings.testDeviceAdvertisingIds =
                arrayListOf(advertisingId)
            testDevice?.let {
                val build = RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(it)).build()
                MobileAds.setRequestConfiguration(build)
            }
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
        adConfig.init()
        val fallBackId = adConfig.fetchFallbackAdUnitId(adName)
        if (isAdActive(adName)) {
            when (adConfig.fetchAdType(adName)) {
                AdType.NATIVE -> NativeAdLoader().preloadNativeAd(
                    context,
                    adName,
                    fallBackId,
                    adConfig.fetchPrimaryAdProvider(adName),
                    contentURL,
                    neighbourContentURL
                )
                AdType.BANNER -> BannerAdLoader().preloadBannerAd(
                    context,
                    adName,
                    adConfig.fetchBannerAdSize(adName),
                    fallBackId,
                    adConfig.fetchPrimaryAdProvider(adName),
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
        activity: Activity? = null,
        adName: String,
        isDarkModeEnabled: Boolean = false,
        showShimmerLoading: Boolean = true,
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
        if (!isSdkInitialized()) {
            val error =
                "${context.getString(R.string.error_sdk_not_initialized)}"
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
        adConfig.init()
        val fallBackId = adConfig.fetchFallbackAdUnitId(adName)
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
                    if (parentView == null && !isNativeFetch) {
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
                        adConfig.fetchPrimaryAdProvider(adName),
                        adConfig.fetchSecondaryAdProvider(adName),
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
                        adsRequested,
                        adConfig.fetchMediaHeight(adName),
                        showShimmerLoading
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
                        adConfig.fetchPrimaryAdProvider(adName),
                        adConfig.fetchSecondaryAdProvider(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        adConfig.fetchAdUnitRefreshTimer(adName),
                        contentURL,
                        neighbourContentURL,
                        bannerAdLoadListener,
                        false,
                        showShimmerLoading
                    )
                }
                AdType.INTERSTITIAL -> {
                    if (activity == null) {
                        val error =
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_activity)}"
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
                    InterstitialAdLoader().loadInterstitialAd(
                        activity,
                        adName,
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchPrimaryAdProvider(adName),
                        adConfig.fetchSecondaryAdProvider(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        interstitialAdLoadListener
                    )
                }
                AdType.REWARDED_INTERSTITIAL ->
                    RewardedInterstitialAdLoader().loadInterstitialRewardedAd(
                        context,
                        adName,
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchPrimaryAdProvider(adName),
                        adConfig.fetchSecondaryAdProvider(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        rewardedInterstitialAdLoadListener
                    )
                AdType.REWARDED -> {
                    if (activity == null) {
                        val error =
                            "$adName ==== $fallBackId ==== ${context.getString(R.string.error_activity)}"
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

                    RewardedAdLoader().loadRewardedAd(
                        context,
                        activity,
                        adName,
                        fallBackId,
                        adConfig.fetchPrimaryAdUnitIds(adName),
                        adConfig.fetchSecondaryAdUnitIds(adName),
                        adConfig.fetchPrimaryAdProvider(adName),
                        adConfig.fetchSecondaryAdProvider(adName),
                        adConfig.fetchAdUnitTimeout(adName),
                        rewardedAdLoadListener
                    )
                }
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
                            adConfig.fetchPrimaryAdProvider(adName),
                            adConfig.fetchSecondaryAdProvider(adName),
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
                        if (!isAppOpenAlreadyRegistered) {
                            isAppOpenAlreadyRegistered = true
                            AppOpenAdLoader().loadAppOpenAdBgToFg(
                                application,
                                context,
                                adName,
                                fallBackId,
                                adConfig.fetchPrimaryAdUnitIds(adName),
                                adConfig.fetchSecondaryAdUnitIds(adName),
                                adConfig.fetchPrimaryAdProvider(adName),
                                adConfig.fetchSecondaryAdProvider(adName),
                                adConfig.fetchBackgroundThreshold(adName)
                            )
                        }else{
                            Logger.e(AdSdkConstants.TAG, "AppOpenAd already registered")
                        }
                    }
                }
                else -> Logger.d(
                    AdSdkConstants.TAG,
                    "$adName ==== $fallBackId ==== ${context.getString(R.string.unknown_ad_type)}"
                )
            }
        } else {
            val error =
                "${context.getString(R.string.error_ad_is_disabled)}"
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

    fun fetchAdUnitId(adName: String): Pair<AdProvider, String>? {
        adConfig.init()
        return if (adConfig.fetchPrimaryAdUnitIds(adName).isNotEmpty()) {
            adConfig.fetchPrimaryAdUnitIds(adName)[0]
            val adProvider = when (adConfig.fetchPrimaryAdProvider(adName)) {
                AdProvider.ADMOB.name.lowercase() -> AdProvider.ADMOB
                AdProvider.APPLOVIN.name.lowercase() -> AdProvider.APPLOVIN
                AdProvider.ADMANAGER.name.lowercase() -> AdProvider.ADMANAGER
                else -> AdProvider.UNKNOWN
            }
            Pair(
                adProvider,
                adConfig.fetchPrimaryAdUnitIds(adName)[0]
            )
        } else {
            null
        }
    }
}