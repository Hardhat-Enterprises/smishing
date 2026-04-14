package com.example.smishingdetectionapp.platform

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

//hold the android application context for settings factory
internal object AndroidContextHolder {
    lateinit var appContext: Context
}

actual fun createSettings(): Settings {
    val prefs = AndroidContextHolder.appContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(prefs)
}