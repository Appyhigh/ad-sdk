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
    var color_hex_dark: String?,
    var bg_color: String?,
    var bg_color_dark: String?,
    var text_color: String?,
    var text_color_dark: String?,
    var size: String?,
    var primary_adload_timeout_ms: Int,
    var background_threshold: Int,
    var mediaHeight: Int
)