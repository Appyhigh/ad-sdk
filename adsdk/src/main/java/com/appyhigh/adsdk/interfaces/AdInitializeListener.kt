package com.appyhigh.adsdk.interfaces

import com.appyhigh.adsdk.data.model.AdSdkError

abstract class AdInitializeListener {
    open fun onSdkInitialized(isHardStopEnabled:Boolean) {}
    open fun onInitializationFailed(adSdkError: AdSdkError) {}
}