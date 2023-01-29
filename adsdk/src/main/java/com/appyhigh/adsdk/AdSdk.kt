package com.appyhigh.adsdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.appyhigh.adsdk.ads.BannerAd
import com.appyhigh.adsdk.data.enums.AdSdkErrorCode
import com.appyhigh.adsdk.data.enums.AdType
import com.appyhigh.adsdk.data.local.SharedPrefs
import com.appyhigh.adsdk.data.model.AdSdkError
import com.appyhigh.adsdk.interfaces.AdInitializeListener
import com.appyhigh.adsdk.interfaces.BannerAdLoadListener
import com.appyhigh.adsdk.interfaces.VersionControlListener
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
                AdType.BANNER -> BannerAd().preloadBannerAd(
                    parentView,
                    adName,
                    adConfig.fetchBannerAdSize(adName),
                    fallBackId,
                    contentURL,
                    neighbourContentURL
                )
                AdType.INTERSTITIAL -> {}
                AdType.REWARDED_INTERSTITIAL -> {}
                AdType.REWARDED -> {}
                AdType.APP_OPEN -> {}
                else -> {}
            }
        }
    }

    fun loadAd(
        parentView: ViewGroup,
        adName: String,
        fallBackId: String,
        contentURL: String? = null,
        neighbourContentURL: List<String>? = null,
        bannerAdLoadListener: BannerAdLoadListener? = null,
    ) {
        adConfig.init()
        if (isAdActive(adName)) {
            when (adConfig.fetchAdType(adName)) {
                AdType.NATIVE -> {}
                AdType.BANNER -> BannerAd().loadBannerAd(
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
                AdType.INTERSTITIAL -> {}
                AdType.REWARDED_INTERSTITIAL -> {}
                AdType.REWARDED -> {}
                AdType.APP_OPEN -> {}
                else -> {}
            }
        }
    }

    private fun isAdActive(adName: String): Boolean {
        return (adConfig.isAppAdsActive() && adConfig.isAdUnitActive(adName))
    }
}