package com.appyhigh.adsdk

import com.appyhigh.adsdk.data.local.SharedPrefs
import com.appyhigh.adsdk.utils.Logger
import com.appyhigh.adsdk.utils.RSAKeyGenerator
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


class DynamicAds {

    fun fetchRemoteAdConfiguration(
        packageId: String
    ) {
        val headerAuthorizationInterceptor = Interceptor { chain ->
            var request = chain.request()
            val builder = request.newBuilder()
            val header =
                request.headers.newBuilder()
                    .add(AdSdkConstants.AUTHORIZATION_HEADER, fetchToken())
                    .build()
            request = builder.headers(header).build()
            chain.proceed(request)
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(headerAuthorizationInterceptor)
        clientBuilder.addInterceptor(interceptor)


        val formBody: RequestBody = FormBody.Builder()
            .add(AdSdkConstants.PACKAGE_ID, packageId)
            .add(AdSdkConstants.PLATFORM, AdSdkConstants.ANDROID)
            .build()

        val request: Request = Request.Builder()
            .url(AdSdkConstants.BASE_URL + "api/v2/app/info")
            .post(formBody)
            .addHeader(AdSdkConstants.AUTHORIZATION_HEADER, fetchToken())
            .build()

        val call: Call = clientBuilder.build().newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val apiResponse = response.body?.string()
                        if (apiResponse != null) {
                            SharedPrefs.putString(
                                AdSdkConstants.AD_CONFIG_RESPONSE,
                                apiResponse
                            )
                        }
                    } else {
                        Logger.e(AdSdkConstants.TAG, response.message)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun fetchToken(): String {
        var token = ""
        try {
            try {
                val map = HashMap<String, String>()
                map["user_id"] = "test_user"
                map["aud"] = "dapps"
                map["api"] = "users"
                token = RSAKeyGenerator.getJwtToken(
                    map,
                    System.currentTimeMillis(),
                    BuildConfig.PRIVATE_KEY_NOTIF
                ) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Bearer $token"
    }
}