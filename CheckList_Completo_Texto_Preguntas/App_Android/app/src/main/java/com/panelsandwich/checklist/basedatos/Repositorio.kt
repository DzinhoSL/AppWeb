package com.panelsandwich.checklist.basedatos

import android.content.ContentValues
import android.util.Log
import com.panelsandwich.checklist.ChecklistApp
import com.panelsandwich.checklist.modelos.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val TAG = "DEBUG_REPOSITORIO"

class Repositorio {

    private val db get() = ChecklistApp.baseDatos.writableDatabase
    private val gson = Gson()

    // ==================== ALMACENES ====================
    suspend fun obtenerAlmacenes(): List<Almacen> {
        Log.d(TAG, "▶ obtenerAlmacenes()")
        return try {
            val lista = mutableListOf<Almacen>()
            db.rawQuery("SELECT id, nombre FROM almacenes", null).use { c ->
                while (c.moveToNext()) lista.add(Almacen(c.getInt(0), c.getString(1)))
            }
            Log.d(TAG, "✅ obtenerAlmacenes() — ${lista.size} resultados")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerAlmacenes(): ${e.message}", e)
            emptyList()
        }
    }

    // ==================== USUARIOS ====================
    suspend fun obtenerUsuariosPorAlmacen(idAlmacen: Int): List<Usuario> {
        Log.d(TAG, "▶ obtenerUsuariosPorAlmacen(idAlmacen=$idAlmacen)")
        return try {
            val lista = mutableListOf<Usuario>()
            db.rawQuery(
                "SELECT id, nombre, rol, id_almacen FROM usuarios WHERE id_almacen = ?",
                arrayOf(idAlmacen.toString())
            ).use { c ->
                while (c.moveToNext()) lista.add(Usuario(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3)))
            }
            Log.d(TAG, "✅ obtenerUsuariosPorAlmacen() — ${lista.size} resultados")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerUsuariosPorAlmacen(): ${e.message}", e)
            emptyList()
        }
    }

    // ==================== MÁQUINAS ====================
    suspend fun obtenerMaquinasPorAlmacen(idAlmacen: Int): List<Maquina> {
        Log.d(TAG, "▶ obtenerMaquinasPorAlmacen(idAlmacen=$idAlmacen)")
        return try {
            val lista = mutableListOf<Maquina>()
            db.rawQuery(
                "SELECT id, nombre, id_almacen FROM maquinas WHERE id_almacen = ?",
                arrayOf(idAlmacen.toString())
            ).use { c ->
                while (c.moveToNext()) lista.add(Maquina(c.getInt(0), c.getString(1), c.getInt(2)))
            }
            Log.d(TAG, "✅ obtenerMaquinasPorAlmacen() — ${lista.size} resultados")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerMaquinasPorAlmacen(): ${e.message}", e)
            emptyList()
        }
    }

    // ==================== CHECKLIST ITEMS ====================
    suspend fun obtenerItemsPorMaquina(idMaquina: Int): List<ItemChecklist> {
        Log.d(TAG, "▶ obtenerItemsPorMaquina(idMaquina=$idMaquina)")
        return try {
            val lista = mutableListOf<ItemChecklist>()
            db.rawQuery(
                "SELECT id, id_maquina, descripcion FROM checklist_items WHERE id_maquina = ?",
                arrayOf(idMaquina.toString())
            ).use { c ->
                while (c.moveToNext()) lista.add(ItemChecklist(c.getInt(0), c.getInt(1), c.getString(2)))
            }
            Log.d(TAG, "✅ obtenerItemsPorMaquina() — ${lista.size} ítems")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerItemsPorMaquina(): ${e.message}", e)
            emptyList()
        }
    }

    // ==================== REVISIONES ====================
    suspend fun crearRevision(revision: Revision): Revision? {
        Log.d(TAG, "▶ crearRevision() — idUsuario=${revision.idUsuario}, idMaquina=${revision.idMaquina}, tieneFallos=${revision.tieneFallos}")
        return try {
            val values = ContentValues().apply {
                put("id_usuario", revision.idUsuario)
                put("id_maquina", revision.idMaquina)
                put("fecha_hora", revision.fechaHora)
                put("firma", revision.firma)
                put("tiene_fallos", if (revision.tieneFallos) 1 else 0)
                put("sincronizado", 0) // pendiente de sincronizar
                put("comentario", revision.comentario)
                put("preguntas_fallidas", gson.toJson(revision.preguntasFallidas)) // Serializar a JSON
            }
            val id = db.insert("revisiones", null, values)
            if (id == -1L) {
                Log.e(TAG, "❌ crearRevision() insert devolvió -1")
                null
            } else {
                Log.d(TAG, "✅ crearRevision() OK — ID local: $id (pendiente de sincronizar)")
                revision.copy(id = id.toInt())
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ crearRevision(): ${e.message}", e)
            null
        }
    }

    /** Devuelve solo las revisiones del usuario realizadas HOY (desde las 05:00 hasta las 04:59 del día siguiente) */
    suspend fun obtenerRevisionesDeHoyPorUsuario(idUsuario: Int): List<Revision> {
        Log.d(TAG, "▶ obtenerRevisionesDeHoyPorUsuario(idUsuario=$idUsuario)")
        return try {
            val lista = mutableListOf<Revision>()
            val type = object : TypeToken<List<String>>() {}.type
            // "Hoy" va de las 05:00 de hoy a las 04:59:59 de mañana
            val ahora = java.util.Calendar.getInstance()
            val cal = java.util.Calendar.getInstance()
            // Si son menos de las 05:00 seguimos en el "día anterior"
            if (ahora.get(java.util.Calendar.HOUR_OF_DAY) < 5) {
                cal.add(java.util.Calendar.DATE, -1)
            }
            cal.set(java.util.Calendar.HOUR_OF_DAY, 5)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            val inicio = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(cal.time)
            cal.add(java.util.Calendar.DATE, 1)
            val fin = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(cal.time)

            db.rawQuery(
                "SELECT id, id_usuario, id_maquina, fecha_hora, firma, tiene_fallos, comentario, preguntas_fallidas FROM revisiones WHERE id_usuario = ? AND fecha_hora >= ? AND fecha_hora < ? ORDER BY fecha_hora DESC",
                arrayOf(idUsuario.toString(), inicio, fin)
            ).use { c ->
                while (c.moveToNext()) {
                    val preguntasFallidasJson = c.getString(7)
                    val preguntasFallidas = gson.fromJson<List<String>>(preguntasFallidasJson, type) ?: emptyList()
                    lista.add(Revision(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), c.getString(4), c.getInt(5) == 1, c.getString(6), preguntasFallidas))
                }
            }
            Log.d(TAG, "✅ obtenerRevisionesDeHoyPorUsuario() — ${lista.size} revisiones hoy (desde $inicio)")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerRevisionesDeHoyPorUsuario(): ${e.message}", e)
            emptyList()
        }
    }

    suspend fun obtenerRevisionesPorUsuario(idUsuario: Int): List<Revision> {
        Log.d(TAG, "▶ obtenerRevisionesPorUsuario(idUsuario=$idUsuario)")
        return try {
            val lista = mutableListOf<Revision>()
            val type = object : TypeToken<List<String>>() {}.type
            db.rawQuery(
                "SELECT id, id_usuario, id_maquina, fecha_hora, firma, tiene_fallos, comentario, preguntas_fallidas FROM revisiones WHERE id_usuario = ? ORDER BY fecha_hora DESC",
                arrayOf(idUsuario.toString())
            ).use { c ->
                while (c.moveToNext()) {
                    val preguntasFallidasJson = c.getString(7)
                    val preguntasFallidas = gson.fromJson<List<String>>(preguntasFallidasJson, type) ?: emptyList()
                    lista.add(Revision(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), c.getString(4), c.getInt(5) == 1, c.getString(6), preguntasFallidas))
                }
            }
            Log.d(TAG, "✅ obtenerRevisionesPorUsuario() — ${lista.size} revisiones")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerRevisionesPorUsuario(): ${e.message}", e)
            emptyList()
        }
    }

    // ==================== DETALLES ====================
    suspend fun crearDetalle(detalle: DetalleRevision): DetalleRevision? {
        Log.d(TAG, "▶ crearDetalle() — idRevision=${detalle.idRevision}, idItem=${detalle.idItem}, resultado=${detalle.resultado}")
        return try {
            val values = ContentValues().apply {
                put("id_revision", detalle.idRevision)
                put("id_item", detalle.idItem)
                put("resultado", if (detalle.resultado) 1 else 0)
                put("sincronizado", 0)
            }
            val id = db.insert("detalles", null, values)
            if (id == -1L) {
                Log.e(TAG, "❌ crearDetalle() insert devolvió -1")
                null
            } else {
                Log.d(TAG, "✅ crearDetalle() OK — ID local: $id")
                detalle.copy(id = id.toInt())
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ crearDetalle(): ${e.message}", e)
            null
        }
    }

    suspend fun obtenerDetallesPorRevision(idRevision: Int): List<DetalleRevision> {
        Log.d(TAG, "▶ obtenerDetallesPorRevision(idRevision=$idRevision)")
        return try {
            val lista = mutableListOf<DetalleRevision>()
            db.rawQuery(
                "SELECT id, id_revision, id_item, resultado FROM detalles WHERE id_revision = ?",
                arrayOf(idRevision.toString())
            ).use { c ->
                while (c.moveToNext()) {
                    lista.add(DetalleRevision(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3) == 1))
                }
            }
            Log.d(TAG, "✅ obtenerDetallesPorRevision() — ${lista.size} detalles")
            lista
        } catch (e: Exception) {
            Log.e(TAG, "❌ obtenerDetallesPorRevision(): ${e.message}", e)
            emptyList()
        }
    }

    // ==================== SINCRONIZACIÓN ====================

    /** Devuelve las revisiones locales que aún no se han enviado al servidor */
    fun obtenerRevisionesPendientes(): List<Revision> {
        val lista = mutableListOf<Revision>()
        val type = object : TypeToken<List<String>>() {}.type
        db.rawQuery(
            "SELECT id, id_usuario, id_maquina, fecha_hora, firma, tiene_fallos, comentario, preguntas_fallidas FROM revisiones WHERE sincronizado = 0",
            null
        ).use { c ->
            while (c.moveToNext()) {
                    val preguntasFallidasJson = c.getString(7)
                    val preguntasFallidas = gson.fromJson<List<String>>(preguntasFallidasJson, type) ?: emptyList()
                    lista.add(Revision(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), c.getString(4), c.getInt(5) == 1, c.getString(6), preguntasFallidas))
            }
        }
        Log.d(TAG, "obtenerRevisionesPendientes() — ${lista.size} pendientes")
        return lista
    }

    /** Devuelve los detalles de una revisión local que aún no se han enviado */
    fun obtenerDetallesPendientesPorRevision(idRevisionLocal: Int): List<DetalleRevision> {
        val lista = mutableListOf<DetalleRevision>()
        db.rawQuery(
            "SELECT id, id_revision, id_item, resultado FROM detalles WHERE id_revision = ? AND sincronizado = 0",
            arrayOf(idRevisionLocal.toString())
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(DetalleRevision(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3) == 1))
            }
        }
        return lista
    }

    /** Marca una revisión local como sincronizada y guarda el ID que le dio el servidor */
    fun marcarRevisionSincronizada(idLocal: Int, idServidor: Int) {
        val values = ContentValues().apply {
            put("sincronizado", 1)
            put("id_servidor", idServidor)
        }
        db.update("revisiones", values, "id = ?", arrayOf(idLocal.toString()))
        Log.d(TAG, "✅ Revisión local $idLocal marcada como sincronizada (id_servidor=$idServidor)")
    }

    /** Marca todos los detalles de una revisión local como sincronizados */
    fun marcarDetallesSincronizados(idRevisionLocal: Int) {
        val values = ContentValues().apply { put("sincronizado", 1) }
        db.update("detalles", values, "id_revision = ?", arrayOf(idRevisionLocal.toString()))
        Log.d(TAG, "✅ Detalles de revisión local $idRevisionLocal marcados como sincronizados")
    }
}
