package com.appyhigh.adsdk.data.model.adresponse

data class AdMob(
    var primary_ids: List<String> = ArrayList(),
    var secondary_ids: List<String> = ArrayList(),
    var _id: String?,
    var ad_name: String?,
    var ad_type: String?,
    var isActive: Boolean = true,
    var refresh_rate_ms: Int?,
    var color_hex: String?,
    var size: String?,
    var primary_adload_timeout_ms: Int
)