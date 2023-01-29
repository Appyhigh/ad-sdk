package com.appyhigh.adsdk.interfaces

import com.appyhigh.adsdk.data.enums.UpdateType

interface VersionControlListener {
    fun onUpdateDetectionSuccess(updateType: UpdateType)
}