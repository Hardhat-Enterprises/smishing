package com.example.smishingdetectionapp.platform

import com.russhwolf.settings.Settings

private const val PREF_NOTIFICATIONS_ENABLED = "pref_notifications_enabled"

object SettingsFacade {
    private val settings: Settings = createSettings()

    var areNotificationsEnabled: Boolean
        get() = settings.getBoolean(PREF_NOTIFICATIONS_ENABLED, true)
        set(value) = settings.putBoolean(PREF_NOTIFICATIONS_ENABLED, value)
}