package com.appyhigh.adsdk

import androidx.core.os.bundleOf
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd

object AdSdkConstants {
    const val BASE_URL = "https://admob-automation.apyhi.com/"
    const val AUTHORIZATION_HEADER = "Authorization"
    const val TAG = "AdSdkLogger"
    const val PACKAGE_ID = "packageId"
    const val PLATFORM = "platform"
    const val ANDROID = "ANDROID"
    const val AD_CONFIG = "ad_config"
    const val AD_CONFIG_RESPONSE = "ad_config_response"
    const val VERSION_CONTROL = "version_control"
    const val CURRENT_VERSION = "current_version"
    const val CRITICAL_VERSION = "critical_version"
    const val PACKAGE_NAME = "package_name"
    const val MY_REQUEST_CODE = 0x121212
    const val NATIVE = "native"
    const val BANNER = "banner"
    const val INTERSTITIAL = "interstitial"
    const val REWARDED_INTERSTITIAL = "rewardinterstitial"
    const val REWARDED = "rewarded"
    const val APP_OPEN = "appopen"
    var adUnitsSet = HashSet<String>()
    var preloadedBannerAdMap = HashMap<String, AdView?>()
    var preloadedNativeAdMap = HashMap<String, NativeAd?>()
    var enableReleaseLogging = false
    var consentStatus = true
    inline val consentDisabledBundle
        get() = bundleOf("npa" to "1")
}