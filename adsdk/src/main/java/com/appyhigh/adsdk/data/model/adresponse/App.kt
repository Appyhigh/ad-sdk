package com.appyhigh.adsdk.data.model.adresponse

data class App(
    var showAppAds: Boolean = true,
    var isActive: Boolean = true,
    var _id: String?,
    var appName: String?,
    var packageId: String?,
    var platform: String?,
    var latestVersion: Int,
    var criticalVersion: Int,
    var appUid: String?,
    var adMob: List<AdMob>?,
)
