package com.panelsandwich.checklist.sincronizacion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "DEBUG_WIFI_RECEIVER"

/**
 * Se activa automáticamente cuando cambia la conectividad de red.
 * Si detecta WiFi, lanza la sincronización de revisiones pendientes.
 */
class WifiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "▶ WifiReceiver.onReceive() — action=${intent.action}")

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }
        val tieneWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (tieneWifi) {
            Log.d(TAG, "📶 WiFi conectado — lanzando sincronización automática")
            CoroutineScope(Dispatchers.IO).launch {
                SincronizadorWifi.sincronizarSiHayWifi(context)
            }
        } else {
            Log.d(TAG, "  Sin WiFi — ignorando")
        }
    }
}
