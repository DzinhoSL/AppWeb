package com.panelsandwich.checklist.sincronizacion

import android.content.Context
import android.util.Log
import com.panelsandwich.checklist.ChecklistApp
import com.panelsandwich.checklist.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "DEBUG_INIT_SYNC"
private const val PREFS_NOMBRE = "sesion_checklist"
private const val CLAVE_DATOS_ENVIADOS = "datos_iniciales_enviados"

object SincronizadorDatosIniciales {

    private val BASE_URL: String
        get() = RetrofitClient.BASE_URL.trimEnd('/')

    /**
     * Comprueba si los datos iniciales ya se enviaron al servidor.
     * Si no se enviaron aún, los lee de la BD local y los sube.
     * Llama a esto una vez al arrancar la app, antes de la sincronización normal.
     */
    suspend fun enviarSiEsPrimeraVez(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
        if (prefs.getBoolean(CLAVE_DATOS_ENVIADOS, false)) {
            Log.d(TAG, "✅ Datos iniciales ya enviados — omitiendo")
            return
        }

        Log.d(TAG, "🚀 Primera ejecución detectada — enviando datos iniciales al servidor...")
        val exito = enviarTodo(context)

        if (exito) {
            prefs.edit().putBoolean(CLAVE_DATOS_ENVIADOS, true).apply()
            Log.d(TAG, "✅ Datos iniciales enviados correctamente — flag guardado")
        } else {
            Log.w(TAG, "⚠ Algunos datos no se pudieron enviar — se reintentará en el próximo arranque")
        }
    }

    private suspend fun enviarTodo(context: Context): Boolean = withContext(Dispatchers.IO) {
        val db = ChecklistApp.baseDatos.readableDatabase
        var todoOk = true

        // ── 1. ALMACENES ──────────────────────────────────────────────
        Log.d(TAG, "▶ Enviando almacenes...")
        // Mapa idLocal → idServidor para usarlo al enviar usuarios y máquinas
        val mapaAlmacenes = mutableMapOf<Int, Int>()

        db.rawQuery("SELECT id, nombre FROM almacenes", null).use { c ->
            while (c.moveToNext()) {
                val idLocal  = c.getInt(0)
                val nombre   = c.getString(1)
                val json     = JSONObject().apply { put("nombre", nombre) }
                val respuesta = post("$BASE_URL/almacenes", json.toString())
                if (respuesta != null) {
                    val idServidor = JSONObject(respuesta).getInt("id")
                    mapaAlmacenes[idLocal] = idServidor
                    Log.d(TAG, "  ✅ Almacén '$nombre' → id_servidor=$idServidor")
                } else {
                    Log.e(TAG, "  ❌ Falló almacén '$nombre'")
                    todoOk = false
                }
            }
        }

        // ── 2. MÁQUINAS ───────────────────────────────────────────────
        Log.d(TAG, "▶ Enviando máquinas...")
        // Mapa idLocal → idServidor para usarlo al enviar checklist_items
        val mapaMaquinas = mutableMapOf<Int, Int>()

        db.rawQuery("SELECT id, nombre, id_almacen FROM maquinas", null).use { c ->
            while (c.moveToNext()) {
                val idLocal    = c.getInt(0)
                val nombre     = c.getString(1)
                val idAlmacenL = c.getInt(2)
                val idAlmacenS = mapaAlmacenes[idAlmacenL]
                if (idAlmacenS == null) {
                    Log.e(TAG, "  ❌ Máquina '$nombre' — no se encontró id_servidor para almacén local $idAlmacenL")
                    todoOk = false
                    continue
                }
                val json = JSONObject().apply {
                    put("nombre", nombre)
                    put("id_almacen", idAlmacenS)
                }
                val respuesta = post("$BASE_URL/maquinas", json.toString())
                if (respuesta != null) {
                    val idServidor = JSONObject(respuesta).getInt("id")
                    mapaMaquinas[idLocal] = idServidor
                    Log.d(TAG, "  ✅ Máquina '$nombre' → id_servidor=$idServidor")
                } else {
                    Log.e(TAG, "  ❌ Falló máquina '$nombre'")
                    todoOk = false
                }
            }
        }

        // ── 3. USUARIOS ───────────────────────────────────────────────
        Log.d(TAG, "▶ Enviando usuarios...")
        db.rawQuery("SELECT nombre, rol, id_almacen FROM usuarios", null).use { c ->
            while (c.moveToNext()) {
                val nombre     = c.getString(0)
                val rol        = c.getString(1)
                val idAlmacenL = c.getInt(2)
                val idAlmacenS = mapaAlmacenes[idAlmacenL]
                if (idAlmacenS == null) {
                    Log.e(TAG, "  ❌ Usuario '$nombre' — no se encontró id_servidor para almacén local $idAlmacenL")
                    todoOk = false
                    continue
                }
                val json = JSONObject().apply {
                    put("nombre", nombre)
                    put("rol", rol ?: JSONObject.NULL)
                    put("id_almacen", idAlmacenS)
                }
                val respuesta = post("$BASE_URL/usuarios", json.toString())
                if (respuesta != null) {
                    Log.d(TAG, "  ✅ Usuario '$nombre'")
                } else {
                    Log.e(TAG, "  ❌ Falló usuario '$nombre'")
                    todoOk = false
                }
            }
        }

        // ── 4. CHECKLIST ITEMS ────────────────────────────────────────
        Log.d(TAG, "▶ Enviando checklist_items...")
        db.rawQuery("SELECT id_maquina, descripcion FROM checklist_items", null).use { c ->
            while (c.moveToNext()) {
                val idMaquinaL = c.getInt(0)
                val descripcion = c.getString(1)
                val idMaquinaS = mapaMaquinas[idMaquinaL]
                if (idMaquinaS == null) {
                    Log.e(TAG, "  ❌ Item '$descripcion' — no se encontró id_servidor para máquina local $idMaquinaL")
                    todoOk = false
                    continue
                }
                val json = JSONObject().apply {
                    put("id_maquina", idMaquinaS)
                    put("descripcion", descripcion)
                }
                val respuesta = post("$BASE_URL/checklist_items", json.toString())
                if (respuesta != null) {
                    Log.d(TAG, "  ✅ Item '$descripcion'")
                } else {
                    Log.e(TAG, "  ❌ Falló item '$descripcion'")
                    todoOk = false
                }
            }
        }

        Log.d(TAG, "📊 Envío de datos iniciales completado — todoOk=$todoOk")
        todoOk
    }

    /** POST HTTP simple, devuelve la respuesta como String o null si falla */
    private fun post(url: String, jsonBody: String): String? {
        return try {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 15_000
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
}
