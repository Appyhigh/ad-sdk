package com.appyhigh.adsdk

import android.app.Application
import com.google.firebase.FirebaseApp
import com.pluto.Pluto
import com.pluto.plugins.exceptions.PlutoExceptionsPlugin
import com.pluto.plugins.logger.PlutoLoggerPlugin
import com.pluto.plugins.network.PlutoNetworkPlugin
import com.pluto.plugins.preferences.PlutoSharePreferencesPlugin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Pluto.Installer(this)
            .addPlugin(PlutoNetworkPlugin())
            .addPlugin(PlutoExceptionsPlugin())
            .addPlugin(PlutoLoggerPlugin())
            .addPlugin(PlutoSharePreferencesPlugin())
            .install()
    }
}