package com.appyhigh.adsdk.interfaces

interface ConsentRequestListener {
    fun onError(message: String, code: Int)
    fun onSuccess()
}