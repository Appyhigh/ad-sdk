package com.appyhigh.adsdk

import android.app.Activity
import android.graphics.Color
import android.view.View
import com.appyhigh.adsdk.AdSdkConstants.AD_CONFIG_RESPONSE
import com.appyhigh.adsdk.AdSdkConstants.MY_REQUEST_CODE
import com.appyhigh.adsdk.data.enums.UpdateType
import com.appyhigh.adsdk.data.local.SharedPrefs
import com.appyhigh.adsdk.data.model.adresponse.AdResponse
import com.appyhigh.adsdk.interfaces.VersionControlListener
import com.appyhigh.adsdk.utils.Logger
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson

class VersionControl {

    private var appUpdateManager: AppUpdateManager? = null
    private var view: View? = null
    private var currentVersion = ""
    private var criticalVersion = ""
    private var firstRequest = true

    fun initializeVersionControl(
        activity: Activity,
        view: View,
        buildVersion: Int,
        versionControlListener: VersionControlListener?
    ) {
        try {
            val gson = Gson()
            if (SharedPrefs.getString(AD_CONFIG_RESPONSE).isNullOrBlank()) {
                return
            }
            val adResponse: AdResponse =
                gson.fromJson(SharedPrefs.getString(AD_CONFIG_RESPONSE), AdResponse::class.java)
            currentVersion = adResponse.app?.latestVersion?.toString() ?: "0"
            criticalVersion = adResponse.app?.criticalVersion?.toString() ?: "0"
            appUpdateManager = AppUpdateManagerFactory.create(activity)
            this.view = view
            if (buildVersion < currentVersion.toFloat().toInt()) {
                when {
                    buildVersion >= criticalVersion.toFloat().toInt() -> {
                        Logger.d(AdSdkConstants.TAG, "SOFT_UPDATE")
                        if (firstRequest) {
                            checkUpdate(
                                activity,
                                AppUpdateType.FLEXIBLE, versionControlListener
                            )
                            firstRequest = false
                        }
                    }
                    buildVersion < criticalVersion.toFloat().toInt() -> {
                        Logger.d(AdSdkConstants.TAG, "HARD_UPDATE")
                        checkUpdate(activity, IMMEDIATE, versionControlListener)
                    }
                    else -> {
                        Logger.d(AdSdkConstants.TAG, "NO_UPDATE")
                        versionControlListener?.onUpdateDetectionSuccess(
                            UpdateType.NO_UPDATE
                        )

                    }
                }
            } else {
                Logger.d(AdSdkConstants.TAG, "NO_UPDATE")
                versionControlListener?.onUpdateDetectionSuccess(
                    UpdateType.NO_UPDATE
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkUpdate(
        activity: Activity,
        updateType: Int,
        versionControlListener: VersionControlListener?
    ) {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        Logger.d(AdSdkConstants.TAG, "Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            Logger.d(AdSdkConstants.TAG, appUpdateInfo.updateAvailability().toString())
            Logger.d(AdSdkConstants.TAG, updateType.toString())
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                if (updateType == IMMEDIATE) {
                    versionControlListener?.onUpdateDetectionSuccess(UpdateType.HARD_UPDATE)
                } else {
                    versionControlListener?.onUpdateDetectionSuccess(UpdateType.SOFT_UPDATE)
                }
                appUpdateManager!!.registerListener(listener)
                appUpdateManager!!.startUpdateFlowForResult(
                    appUpdateInfo,
                    activity,
                    AppUpdateOptions.newBuilder(updateType)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                    MY_REQUEST_CODE
                )
            } else {
                Logger.d(AdSdkConstants.TAG, "No Update available")
                versionControlListener?.onUpdateDetectionSuccess(UpdateType.NO_UPDATE)
            }
        }
    }

    private val listener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackBarForCompleteUpdate()
            }
        }

    private fun showSnackBarForCompleteUpdate() {
        Logger.d(AdSdkConstants.TAG, "An update has been downloaded")
        appUpdateManager!!.unregisterListener(listener)
        view?.let {
            Snackbar.make(
                it,
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction("RESTART") { appUpdateManager!!.completeUpdate() }
                setActionTextColor(Color.WHITE)
                show()
            }
        }
    }
}