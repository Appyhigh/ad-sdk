package com.appyhigh.adsdk.utils

import android.annotation.SuppressLint
import com.appyhigh.adsdk.AdSdkConstants
import com.appyhigh.adsdk.data.enums.AdType
import com.appyhigh.adsdk.data.enums.BannerAdSize
import com.appyhigh.adsdk.data.local.SharedPrefs
import com.appyhigh.adsdk.data.model.adresponse.AdMob
import com.appyhigh.adsdk.data.model.adresponse.AdResponse
import com.google.android.gms.ads.AdSize
import com.google.gson.Gson

class AdConfig {
    var adResponse: AdResponse? = null
    var adsMap = HashMap<String, AdMob>()
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

}