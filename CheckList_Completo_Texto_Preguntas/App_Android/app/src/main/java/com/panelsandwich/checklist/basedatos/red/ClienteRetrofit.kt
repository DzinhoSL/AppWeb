package com.panelsandwich.checklist.basedatos.red

/**
 * ClienteRetrofit - DESACTIVADO (capa antigua)
 * La sincronización activa usa:
 *   - com.panelsandwich.checklist.api.RetrofitClient  → para llamadas Retrofit
 *   - com.panelsandwich.checklist.sincronizacion.SincronizadorWifi → para sincronización offline→online
 *
 * Esta clase se mantiene por compatibilidad pero no se usa.
 */
object ClienteRetrofit {
    val apiServicio: ApiServicio
        get() = throw UnsupportedOperationException("Capa desactivada. Usa RetrofitClient o SincronizadorWifi.")
}
