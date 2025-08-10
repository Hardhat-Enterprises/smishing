package com.example.smishingdetectionapp.Connectivity

import android.content.Context
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ConnectivityMonitor {
    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> get() = _isConnected

    @JvmStatic fun getIsConnected(): LiveData<Boolean> = isConnected

    @JvmStatic
    fun init(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Seed initial value
        _isConnected.postValue(isNetworkOnline(cm))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isConnected.postValue(isNetworkOnline(cm))
                }
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    _isConnected.postValue(isNetworkOnline(cm))
                }
                override fun onLost(network: Network) {
                    _isConnected.postValue(false)
                }
                override fun onUnavailable() {
                    _isConnected.postValue(false)
                }
            })
        } else {
            // For pre-N we can’t register a default callback reliably; we seed only.
            _isConnected.postValue(isNetworkOnline(cm))
        }
    }

    private fun isNetworkOnline(cm: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val nc = cm.getNetworkCapabilities(network) ?: return false

            val hasTransport = nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

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
