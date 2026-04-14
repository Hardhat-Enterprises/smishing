package com.example.smishingdetectionapp

import com.russhwolf.settings.Settings
import org.koin.java.KoinJavaComponent.get

//helper object to access Koin dependencies from Jafva code.
//this allows java classes to get injected dependencies

object KoinHelper{
    //provides the shared settings instance managed by Koin
    //we use the fully qualified name of the interface for java interoperability
    val settings: Settings = get(Settings::class.java)
}
