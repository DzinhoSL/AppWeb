package com.panelsandwich.checklist.basedatos.red

import com.panelsandwich.checklist.modelos.*

/**
 * ApiServicio - DESACTIVADO (modo offline con SQLite local)
 * Interfaz reservada para cuando se migre a servidor propio.
 * Reactiva las anotaciones de Retrofit y las dependencias en build.gradle al migrar.
 */
interface ApiServicio {
    suspend fun obtenerAlmacenes(): Any
    suspend fun obtenerUsuariosPorAlmacen(idAlmacen: Int): Any
    suspend fun obtenerMaquinasPorAlmacen(idAlmacen: Int): Any
    suspend fun obtenerItemsPorMaquina(idMaquina: Int): Any
    suspend fun crearRevision(revision: Revision): Any
    suspend fun obtenerRevisionesPorUsuario(idUsuario: Int): Any
    suspend fun crearDetalle(detalle: DetalleRevision): Any
    suspend fun obtenerDetallesPorRevision(idRevision: Int): Any
}
