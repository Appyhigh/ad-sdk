package com.appyhigh.adsdk.utils

import android.annotation.SuppressLint
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.data.enums.AdType
import com.appyhigh.adsdk.data.enums.BannerAdSize
import com.appyhigh.adsdk.data.enums.NativeAdSize
import com.appyhigh.adsdk.data.local.SharedPrefs
import com.appyhigh.adsdk.data.model.adresponse.AdMob
import com.appyhigh.adsdk.data.model.adresponse.AdResponse
import com.google.android.gms.ads.AdSize
import com.google.gson.Gson

internal class AdConfig {
    private var adResponse: AdResponse? = null
    private var adsMap = HashMap<String, AdMob>()
    private var defaultLightCTAHex = "#000000"
    private var defaultDarkCTAHex = "#ffffff"
    private var defaultLightTextHex = "#ffffff"
    private var defaultDarkTextHex = "#000000"
    private var defaultLightBackgroundHex = "#ffffff"
    private var defaultDarkBackgroundHex = "#000000"
    private var defaultPrimaryAdProvider = "applovin"
    private var defaultSecondaryAdProvider = "applovin"

    fun initWithLocalFile(fileData: String) {
        if (adResponse == null) {
            if (SharedPrefs.getString(AdSdkConstants.AD_CONFIG_RESPONSE).isNullOrBlank()) {
                Logger.d(AdSdkConstants.TAG, "Cache Data set using local file")
                SharedPrefs.putString(AdSdkConstants.AD_CONFIG_RESPONSE, fileData)
            }
        }
    }

    fun init() {
        if (adResponse == null) {
            val gson = Gson()
            if (SharedPrefs.getString(AdSdkConstants.AD_CONFIG_RESPONSE).isNullOrBlank()) {
                return
            }
            adResponse =
                gson.fromJson(
                    SharedPrefs.getString(AdSdkConstants.AD_CONFIG_RESPONSE),
                    AdResponse::class.java
                )
            adResponse?.let {
                for (adItem in it.app?.adMob!!) {
                    adsMap[adItem.ad_name!!] = adItem
                }
                Logger.d(AdSdkConstants.TAG, "Cache Data Updated using api response")
            }
        }
    }

    fun isAppAdsActive(): Boolean {
        return adResponse?.app?.showAppAds ?: true
    }

    fun isAdUnitActive(adName: String): Boolean {
        adResponse?.let {
            return adsMap[adName]?.isActive ?: true
        }
        return true
    }

    fun fetchAdType(adName: String): AdType? {
        adResponse?.let {
            adsMap[adName]?.ad_type?.let { adType ->
                when (adType.lowercase()) {
                    AdSdkConstants.NATIVE -> return AdType.NATIVE
                    AdSdkConstants.BANNER -> return AdType.BANNER
                    AdSdkConstants.INTERSTITIAL -> return AdType.INTERSTITIAL
                    AdSdkConstants.REWARDED_INTERSTITIAL -> return AdType.REWARDED_INTERSTITIAL
                    AdSdkConstants.REWARDED -> return AdType.REWARDED
                    AdSdkConstants.APP_OPEN -> return AdType.APP_OPEN
                    else -> return null
                }
            }
        }
        return null
    }

    @SuppressLint("VisibleForTests")
    fun fetchBannerAdSize(adName: String): AdSize {
        adResponse?.let {
            adsMap[adName]?.size?.let { adSize ->
                when (adSize) {
                    BannerAdSize.BANNER.name.lowercase() -> return AdSize.BANNER
                    BannerAdSize.MEDIUM_RECTANGLE.name.lowercase() -> return AdSize.MEDIUM_RECTANGLE
                    BannerAdSize.LARGE_BANNER.name.lowercase() -> return AdSize.LARGE_BANNER
                    else -> return AdSize.BANNER
                }
            }
        }
        return AdSize.BANNER
    }

    fun fetchNativeAdSize(adName: String): NativeAdSize {
        adResponse?.let {
            adsMap[adName]?.size?.let { adSize ->
                when (adSize) {
                    NativeAdSize.SMALL.name.lowercase() -> return NativeAdSize.SMALL
                    NativeAdSize.MEDIUM.name.lowercase() -> return NativeAdSize.MEDIUM
                    NativeAdSize.BIGV1.name.lowercase() -> return NativeAdSize.BIGV1
                    NativeAdSize.BIGV2.name.lowercase() -> return NativeAdSize.BIGV2
                    NativeAdSize.BIGV3.name.lowercase() -> return NativeAdSize.BIGV3
                    NativeAdSize.GRID_AD.name.lowercase() -> return NativeAdSize.GRID_AD
                    else -> return NativeAdSize.DEFAULT
                }
            }
        }
        return NativeAdSize.DEFAULT
    }

    fun fetchFallbackAdUnitId(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.primary_ids?.get(0) ?: ""
        }
        return ""
    }

    fun fetchPrimaryAdUnitIds(adName: String): List<String> {
        adResponse?.let {
            return adsMap[adName]?.primary_ids ?: emptyList()
        }
        return emptyList()
    }

    fun fetchSecondaryAdUnitIds(adName: String): List<String> {
        adResponse?.let {
            return adsMap[adName]?.secondary_ids ?: emptyList()
        }
        return emptyList()
    }

    fun fetchAdUnitTimeout(adName: String): Int {
        adResponse?.let {
            return adsMap[adName]?.primary_adload_timeout_ms ?: 10000
        }
        return 10000
    }

    fun fetchAdUnitRefreshTimer(adName: String): Int {
        adResponse?.let {
            return adsMap[adName]?.refresh_rate_ms ?: 45000
        }
        return 45000
    }

    fun fetchLightCTAColor(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.color_hex ?: defaultLightCTAHex
        }
        return defaultLightCTAHex
    }

    fun fetchDarkCTAColor(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.color_hex_dark ?: defaultDarkCTAHex
        }
        return defaultDarkCTAHex
    }

    fun fetchLightTextColor(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.text_color ?: defaultLightTextHex
        }
        return defaultLightTextHex
    }

    fun fetchDarkTextColor(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.text_color_dark ?: defaultDarkTextHex
        }
        return defaultDarkTextHex
    }

    fun fetchLightBackgroundColor(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.bg_color ?: defaultLightBackgroundHex
        }
        return defaultLightBackgroundHex
    }

    fun fetchDarkBackgroundColor(adName: String): String {
        adResponse?.let {
            return adsMap[adName]?.bg_color_dark ?: defaultDarkBackgroundHex
        }
        return defaultDarkBackgroundHex
    }

    fun fetchBackgroundThreshold(adName: String): Int {
        adResponse?.let {
            return adsMap[adName]?.background_threshold ?: 4000
        }
        return 4000
    }

    fun fetchMediaHeight(adName: String): Int {
        adResponse?.let {
            return adsMap[adName]?.mediaHeight ?: 300
        }
        return 300
    }

    fun fetchPrimaryAdProvider(adName: String): String {
        adResponse?.let {
            return defaultPrimaryAdProvider
        }
        return defaultPrimaryAdProvider
    }

    fun fetchSecondaryAdProvider(adName: String): String {
        adResponse?.let {
            return defaultSecondaryAdProvider
        }
        return defaultSecondaryAdProvider
    }

}