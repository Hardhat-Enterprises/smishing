package com.example.smishingdetectionapp.Connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Global connectivity monitor (Kotlin) that Java can call.
 * Exposes @JvmStatic methods so MainActivity.java can use:
 *   ConnectivityMonitor.init(context)
 *   ConnectivityMonitor.getIsConnected().observe(...)
 */
object ConnectivityMonitor {

    private lateinit var appContext: Context
    private var initialized = false

    private val isConnectedLive = MutableLiveData<Boolean>(true)

    @JvmStatic
    fun init(context: Context) {
        if (initialized) return
        appContext = context.applicationContext
        initialized = true

        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Set initial value
        isConnectedLive.postValue(currentConnected(cm))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Default network callbacks on modern Android
            cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnectedLive.postValue(true)
                }
                override fun onLost(network: Network) {
                    isConnectedLive.postValue(currentConnected(cm))
                }
            })
        } else {
            // Fallback for old devices: listen for CONNECTIVITY_ACTION broadcasts
            @Suppress("DEPRECATION")
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            appContext.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    isConnectedLive.postValue(currentConnected(cm))
                }
            }, filter)
        }
    }

    @JvmStatic
    fun getIsConnected(): LiveData<Boolean> = isConnectedLive

    private fun currentConnected(cm: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val n = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(n) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnected == true
        }
    }
}
