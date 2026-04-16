package com.panelsandwich.checklist.utilidades

import android.content.Context
import android.util.Log

class TareaNotificacion(private val context: Context) {

    companion object {
        const val NOMBRE_TAREA = "tarea_recordatorio_checklist"
        const val EMAIL_JEFE = "david@panelsandwich.com"
    }

    suspend fun verificarChecklistAlmacen(idAlmacen: Int, nombreAlmacen: String): Boolean {
        return try {
            Log.d(NOMBRE_TAREA, "Verificando check-list del almacén: $nombreAlmacen")
            Log.i(NOMBRE_TAREA, "Notificación simulada enviada a $EMAIL_JEFE por ausencia de check-list en $nombreAlmacen")
            true
        } catch (e: Exception) {
            Log.e(NOMBRE_TAREA, "Error al enviar notificación: ${e.message}")
            false
        }
    }
}
