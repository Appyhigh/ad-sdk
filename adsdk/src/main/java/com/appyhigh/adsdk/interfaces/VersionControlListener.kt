package com.appyhigh.adsdk.interfaces

import com.appyhigh.adsdk.data.enums.UpdateType

abstract class VersionControlListener {
    open fun onUpdateDetectionSuccess(updateType: UpdateType){}
}