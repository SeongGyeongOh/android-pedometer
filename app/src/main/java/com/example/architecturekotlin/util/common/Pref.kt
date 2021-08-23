package com.example.architecturekotlin.util.common

import android.content.Context
import android.content.SharedPreferences
import com.example.architecturekotlin.BuildConfig
import javax.inject.Inject

open class Pref @Inject constructor(
    private val context: Context
) {

    fun setValue(key: String, value: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(getKey(), 0)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setBoolValue(key: String, value: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(getKey(), 0)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setIntValue(key: String, value: Int) {
        val prefs: SharedPreferences = context.getSharedPreferences(getKey(), 0)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getValue(key: String): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(getKey(), 0)
        return prefs.getString(key, null)
    }

    fun getBoolVal(key: String): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(getKey(), 0)
        return prefs.getBoolean(key, true)
    }

    fun getIntValue(key: String): Int {
        val prefs: SharedPreferences = context.getSharedPreferences(getKey(), 0)
        return prefs.getInt(key, 0)
    }

    private fun getKey(): String {
        return "${BuildConfig.APPLICATION_ID}.PREF"
    }
}