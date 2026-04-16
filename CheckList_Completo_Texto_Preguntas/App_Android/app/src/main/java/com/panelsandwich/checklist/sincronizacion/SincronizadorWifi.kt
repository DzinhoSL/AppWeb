package com.panelsandwich.checklist.sincronizacion

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.panelsandwich.checklist.api.RetrofitClient
import com.panelsandwich.checklist.basedatos.Repositorio
import com.panelsandwich.checklist.modelos.DetalleRevision
import com.panelsandwich.checklist.modelos.Revision
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "DEBUG_SYNC"

object SincronizadorWifi {

    // La URL base se toma de RetrofitClient para no duplicarla.
    // Quita la barra final para construir las rutas POST correctamente.
    private val BASE_URL: String
        get() = RetrofitClient.BASE_URL.trimEnd('/')

    private val repositorio = Repositorio()
    private val gson = Gson()
    private var enCurso = false

    /**
     * Comprueba si hay WiFi y, si es así, sincroniza todas las revisiones pendientes.
     * Llama a esto desde cualquier punto de la app (al iniciar, al guardar revisión, etc.)
     */
    suspend fun sincronizarSiHayWifi(context: Context) {
        if (enCurso) { Log.d(TAG, "⚡ Sincronización ya en curso, ignorando"); return }
        if (!hayWifi(context)) { Log.d(TAG, "⚡ Sin WiFi — sincronización pospuesta"); return }
        enCurso = true
        try {
            Log.d(TAG, "📶 WiFi disponible — iniciando sincronización")
            sincronizar()
        } finally {
            enCurso = false
        }
    }

    private suspend fun sincronizar() = withContext(Dispatchers.IO) {
        val pendientes = repositorio.obtenerRevisionesPendientes()
        if (pendientes.isEmpty()) {
            Log.d(TAG, "✅ Nada pendiente de sincronizar")
            return@withContext
        }

        Log.d(TAG, "▶ Sincronizando ${pendientes.size} revisiones pendientes...")
        var sincronizadas = 0
        var fallidas = 0

        for (revisionLocal in pendientes) {
            Log.d(TAG, "  → Revisión local ID=${revisionLocal.id}, maquina=${revisionLocal.idMaquina}, usuario=${revisionLocal.idUsuario}")
            try {
                // 1. Enviar la revisión al servidor
                val idServidor = enviarRevision(revisionLocal)
                if (idServidor == null) {
                    Log.e(TAG, "  ❌ No se pudo enviar revisión local ${revisionLocal.id}")
                    fallidas++
                    continue
                }
                Log.d(TAG, "  ✅ Revisión enviada — id_servidor=$idServidor")

                // 2. Enviar sus detalles usando el ID que devolvió el servidor
                val detalles = repositorio.obtenerDetallesPendientesPorRevision(revisionLocal.id)
                Log.d(TAG, "     Enviando ${detalles.size} detalles...")
                var todosDetallesOk = true
                for (detalle in detalles) {
                    val detalleConIdServidor = detalle.copy(idRevision = idServidor)
                    val ok = enviarDetalle(detalleConIdServidor)
                    if (!ok) {
                        Log.e(TAG, "     ❌ Falló detalle idItem=${detalle.idItem}")
                        todosDetallesOk = false
                    }
                }

                // 3. Marcar como sincronizado en local solo si todo fue bien
                if (todosDetallesOk) {
                    repositorio.marcarRevisionSincronizada(revisionLocal.id, idServidor)
                    repositorio.marcarDetallesSincronizados(revisionLocal.id)
                    sincronizadas++
                    Log.d(TAG, "  ✅ Revisión local ${revisionLocal.id} completamente sincronizada")
                } else {
                    Log.w(TAG, "  ⚠ Revisión local ${revisionLocal.id} — algunos detalles fallaron, se reintentará")
                    fallidas++
                }

            } catch (e: Exception) {
                Log.e(TAG, "  ❌ Excepción sincronizando revisión ${revisionLocal.id}: ${e.message}", e)
                fallidas++
            }
        }

        Log.d(TAG, "📊 Sincronización terminada — OK: $sincronizadas, Fallidas: $fallidas")
    }

    /**
     * Envía una revisión al servidor.
     * Los campos del JSON deben coincidir exactamente con lo que espera tu API PHP.
     * Esquema servidor: id_usuario, id_maquina, fecha_hora, firma, tiene_fallos
     */
    private fun enviarRevision(revision: Revision): Int? {
        return try {
            val json = JSONObject().apply {
                put("id_usuario", revision.idUsuario)
                put("id_maquina", revision.idMaquina)
                put("fecha_hora", revision.fechaHora)
                put("firma", revision.firma ?: JSONObject.NULL)
                put("tiene_fallos", revision.tieneFallos)
                put("comentario", revision.comentario ?: JSONObject.NULL)
                put("preguntas_fallidas", gson.toJson(revision.preguntasFallidas))
            }
            val respuesta = post("$BASE_URL/revisiones", json.toString())
            if (respuesta != null) {
                JSONObject(respuesta).getInt("id")
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "enviarRevision excepción: ${e.message}")
            null
        }
    }

    /**
     * Envía un detalle al servidor.
     * Los campos del JSON deben coincidir con tu API PHP.
     * Esquema servidor: id_revision, id_item, resultado
     */
    private fun enviarDetalle(detalle: DetalleRevision): Boolean {
        return try {
            val json = JSONObject().apply {
                put("id_revision", detalle.idRevision)
                put("id_item", detalle.idItem)
                put("resultado", detalle.resultado)

            }
            post("$BASE_URL/detalles", json.toString()) != null
        } catch (e: Exception) {
            Log.e(TAG, "enviarDetalle excepción: ${e.message}")
            false
        }
    }

    /** Hace un POST HTTP y devuelve la respuesta como String, o null si falla */
    private fun post(url: String, jsonBody: String): String? {
        return try {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            OutputStreamWriter(conn.outputStream).use { it.write(jsonBody) }
            val code = conn.responseCode
            Log.d(TAG, "  POST $url → HTTP $code")
            if (code in 200..299) {
                conn.inputStream.bufferedReader().readText()
            } else {
                val error = conn.errorStream?.bufferedReader()?.readText() ?: "sin detalle"
                Log.e(TAG, "  POST $url falló HTTP $code: $error")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "  POST $url excepción: ${e.message}")
            null
        }
    }

    /** Comprueba si el dispositivo tiene WiFi activo */
    private fun hayWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}
