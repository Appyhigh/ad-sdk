package com.appyhigh.adsdk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appyhigh.adsdk.data.enums.UpdateType
import com.appyhigh.adsdk.interfaces.AdInitializeListener
import com.appyhigh.adsdk.interfaces.ConsentRequestListener
import com.appyhigh.adsdk.interfaces.VersionControlListener

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AdSdk.getConsentForEU(
            activity = this,
            "1EA55AD5EAAA03034C15481D2B68CBED",
            object : ConsentRequestListener {
                override fun onError(message: String, code: Int) {}
                override fun onSuccess() {
                    initializeAdSdk()
                }
            }
        )
    }

    fun initializeAdSdk() {
        AdSdk.initialize(
            application = application,
            testDevice = null,
            fileId = R.raw.ad_utils_response,
            adInitializeListener = object : AdInitializeListener() {
                override fun onSdkInitialized() {
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