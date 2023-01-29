package com.appyhigh.adsdk.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.appyhigh.adsdk.AdSdkConstants

object SharedPrefs {
    private lateinit var mPref: SharedPreferences
    @JvmStatic
    fun init(context: Context) {
        mPref = context.getSharedPreferences(AdSdkConstants.AD_CONFIG, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun putString(key: String, value: String) {
        val editor = mPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    fun putLong(key: String, value: Long) {
        val editor = mPref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    @JvmStatic
    fun putInt(key: String, value: Int) {
        val editor = mPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    @SuppressLint("ApplySharedPref")
    @JvmStatic
    fun putBoolean(key: String, value: Boolean) {
        val editor = mPref.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    @JvmStatic
    fun getBoolean(key: String): Boolean {
        return mPref.getBoolean(key, false)
    }

    @JvmStatic
    fun getBoolean(key: String, def: Boolean): Boolean {
        return mPref.getBoolean(key, def)
    }

    @JvmStatic
    fun getString(key: String): String? {
        return mPref.getString(key, "")
    }

    @JvmStatic
    fun getString(key: String, def: String?): String? {
        return mPref.getString(key, def)
    }

    @JvmStatic
    fun getLong(key: String): Long {
        return mPref.getLong(key, 0)
    }

    @JvmStatic
    fun getLong(key: String, defInt: Int): Long {
        return mPref.getLong(key, defInt.toLong())
    }

    @JvmStatic
    fun getInt(key: String): Int {
        return mPref.getInt(key, 0)
    }

    @JvmStatic
    fun getInt(key: String, defInt: Int): Int {
        return mPref.getInt(key, defInt)
    }

    @JvmStatic
    operator fun contains(key: String): Boolean {
        return mPref.contains(key)
    }

    @JvmStatic
    fun remove(key: String) {
        val editor = mPref.edit()
        editor.remove(key)
        editor.apply()
    }

    @JvmStatic
    fun clear() {
        val editor = mPref.edit()
        editor.clear()
        editor.apply()
    }

}
