package com.panelsandwich.checklist

import android.app.Application
import android.util.Log
import com.panelsandwich.checklist.basedatos.BaseDatosLocal
import com.panelsandwich.checklist.sincronizacion.SincronizadorDatosIniciales
import com.panelsandwich.checklist.sincronizacion.SincronizadorWifi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChecklistApp : Application() {
    companion object {
        lateinit var baseDatos: BaseDatosLocal
    }

    override fun onCreate() {
        super.onCreate()
        baseDatos = BaseDatosLocal(this)
        Log.d("DEBUG_APP", "✅ BaseDatosLocal inicializada")

        CoroutineScope(Dispatchers.IO).launch {
            // 1. Primera vez: enviar almacenes, máquinas, usuarios e items al servidor
            SincronizadorDatosIniciales.enviarSiEsPrimeraVez(applicationContext)
            // 2. Sincronizar revisiones pendientes (modo offline → online)
            SincronizadorWifi.sincronizarSiHayWifi(applicationContext)
        }
    }
}
