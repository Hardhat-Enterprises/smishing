package com.example.smishingdetectionapp.platform

import com.russhwolf.settings.Settings
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.Foundation.NSUserDefaults

actual fun createSettings(): Settings {
    //Use the standard UserDefaults for the app
    return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}