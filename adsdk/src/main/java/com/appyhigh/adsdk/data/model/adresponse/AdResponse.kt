package com.appyhigh.adsdk.data.model.adresponse

import androidx.annotation.Keep

@Keep
data class AdResponse(
    var status: String?,
    var message: String?,
    var app: App?
)
