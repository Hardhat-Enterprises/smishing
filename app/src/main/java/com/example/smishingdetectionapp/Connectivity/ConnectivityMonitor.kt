package com.example.smishingdetectionapp.Connectivity

import android.content.Context
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ConnectivityMonitor {
    // Backing LiveData (stays the same for Java interop)
    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> get() = _isConnected

    // Cached last-emitted value so we only notify on *real* changes
    @Volatile private var lastEmitted: Boolean? = null

    @JvmStatic fun getIsConnected(): LiveData<Boolean> = isConnected

    @JvmStatic
    fun init(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Seed initial value once (guarded)
        emitIfChanged(isNetworkOnline(cm))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    emitIfChanged(isNetworkOnline(cm))
                }
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    emitIfChanged(isNetworkOnline(cm))
                }
                override fun onLost(network: Network) {
                    emitIfChanged(false)
                }
                override fun onUnavailable() {
                    emitIfChanged(false)
                }
            })
        } else {
            // Pre-N: no reliable default callback. We seed once; screens can still query the current value.
            emitIfChanged(isNetworkOnline(cm))
        }
    }

    /** Posts to LiveData only when the value actually changes. */
    private fun emitIfChanged(newValue: Boolean) {
        val prev = lastEmitted
        if (prev == null || prev != newValue) {
            lastEmitted = newValue
            _isConnected.postValue(newValue)
        }
    }

    private fun isNetworkOnline(cm: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val nc = cm.getNetworkCapabilities(network) ?: return false

            val hasTransport =
                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

            // INTERNET means the network *claims* it can reach the internet; VALIDATED means it was actually validated.
            // Using (INTERNET OR VALIDATED) keeps behavior aligned with your original logic.
            val hasInternetOrValidated =
                nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
                        nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            hasTransport && hasInternetOrValidated
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnected == true
        }
    }
}
