package com.appyhigh.adsdk.data.model

import com.appyhigh.adsdk.data.enums.AdSdkErrorCode

data class AdSdkError(
    var errorCode: AdSdkErrorCode,
    var message: String
)
