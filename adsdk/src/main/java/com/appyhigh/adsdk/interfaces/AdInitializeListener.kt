package com.appyhigh.adsdk.interfaces

import com.appyhigh.adsdk.data.model.AdSdkError

interface AdInitializeListener {
    fun onSdkInitialized()
    fun onInitializationFailed(adSdkError: AdSdkError)
}