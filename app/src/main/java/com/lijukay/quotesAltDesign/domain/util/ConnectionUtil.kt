package com.lijukay.quotesAltDesign.domain.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

interface ConnectionHelper {
    val isConnected: Boolean
}

class ConnectionHelperImpl(
    private val context: Context
) : ConnectionHelper {
    override val isConnected: Boolean
        get() {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworks = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetworks) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
}