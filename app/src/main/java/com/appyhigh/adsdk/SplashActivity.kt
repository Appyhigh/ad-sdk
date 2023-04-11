package com.appyhigh.adsdk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.appyhigh.adsdk.data.enums.UpdateType
import com.appyhigh.adsdk.interfaces.AdInitializeListener
import com.appyhigh.adsdk.interfaces.BypassAppOpenAd
import com.appyhigh.adsdk.interfaces.ConsentRequestListener
import com.appyhigh.adsdk.interfaces.VersionControlListener
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(),BypassAppOpenAd {
    var advertId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d(AdSdkConstants.TAG, "start")
        CoroutineScope(Dispatchers.IO).launch {
            var idInfo: AdvertisingIdClient.Info? = null
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                advertId = idInfo!!.id
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

            initializeAdSdk()
        }
    }

    fun initializeAdSdk() {
        AdSdk.initialize(
            application = application,
            testDevice = "D3D0E14422C7B10ADA2BAA773B14EFB6",
            advertisingId = advertId,
            fileId = R.raw.ad_utils_response,
            adInitializeListener = object : AdInitializeListener() {
                override fun onSdkInitialized() {
                    Log.d(AdSdkConstants.TAG, "end")
                    AdSdk.setUpVersionControl(
                        activity = this@SplashActivity,
                        view = findViewById(R.id.tvInto),
                        buildVersion = BuildConfig.VERSION_CODE,
                        versionControlListener = object : VersionControlListener() {
                            override fun onUpdateDetectionSuccess(updateType: UpdateType) {
                                when (updateType) {
                                    UpdateType.SOFT_UPDATE -> {
                                    }
                                    UpdateType.HARD_UPDATE -> {
                                    }
                                    else -> {}
                                }
                            }
                        }
                    )
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        )
    }
}