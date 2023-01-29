package com.appyhigh.adsdk.utils

import android.os.Build
import android.util.Base64
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.GeneralSecurityException
import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import java.util.concurrent.TimeUnit

object RSAKeyGenerator {
    private val TAG = RSAKeyGenerator::class.java.canonicalName

    private fun privateKey(privateKey: String): PrivateKey {
        var kf = KeyFactory.getInstance("RSA")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            kf = KeyFactory.getInstance("RSA", "BC")
        }
        val decode: ByteArray = Base64.decode(privateKey, Base64.DEFAULT)
        val keySpecPKCS8 = PKCS8EncodedKeySpec(decode)
        return kf.generatePrivate(keySpecPKCS8)
    }

    /*appId: String?, userId: String?,device_model :String,network : String*/
    fun getJwtToken(
        data: HashMap<String, String>,
        prevIAT: Long,
        privateKeyNotif: String
    ): String? {
        val validityMs = TimeUnit.MINUTES.toMillis(60)
        val userId = data["user_id"]
        val api = data["api"]
        val prevJwt = data["jwt_token"]
        val exp: Date
        var prevIAT: Long = 0

        //get the real time in unix epoch format (milliseconds since midnight on 1 january 1970)
        val nowMillis: Long = System.currentTimeMillis()
        val iat = Date(nowMillis)
        exp = Date(nowMillis + validityMs)
        val timeOutInMinutes = 50
        return if (nowMillis - prevIAT > timeOutInMinutes * 60 * 1000) {
            var privateKey: Key? = null
            try {
                privateKey = privateKey(privateKeyNotif)
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
            }
            val jws = Jwts.builder()
                .claim("user_id", userId)
                .claim("api", api)
                .claim("iat", iat)
                .claim("exp", exp)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setAudience("adutils")
                .compact()
            jws
        } else {
            prevJwt?.let {
            }
            prevJwt
        }
    }
}