package com.panelsandwich.checklist.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.basedatos.Repositorio
import com.panelsandwich.checklist.modelos.*
import com.panelsandwich.checklist.utilidades.Utilidades
import com.panelsandwich.checklist.vistas.VistaFirma
import kotlinx.coroutines.launch

private const val TAG = "DEBUG_FIRMA"

class ActividadFirma : AppCompatActivity() {

    private lateinit var vistaFirma: VistaFirma
    private lateinit var botonLimpiar: Button
    private lateinit var botonEnviar: Button
    private lateinit var botonAtras: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var usuario: Usuario
    private lateinit var almacen: Almacen
    private lateinit var maquina: Maquina

    private val repositorio = Repositorio()

    companion object {
        const val EXTRA_USUARIO = "usuario"
        const val EXTRA_ALMACEN = "almacen"
        const val EXTRA_MAQUINA = "maquina"
        const val EXTRA_COMENTARIO_GENERAL = "comentario_general"
        const val EXTRA_HAY_FALLOS = "hay_fallos"
        const val EXTRA_PREGUNTAS_FALLIDAS = "preguntas_fallidas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "▶ ActividadFirma onCreate()")
        setContentView(R.layout.actividad_firma)

        // Recuperar datos del intent
        usuario = intent.getSerializableExtra(EXTRA_USUARIO) as Usuario
        almacen = intent.getSerializableExtra(EXTRA_ALMACEN) as Almacen
        maquina = intent.getSerializableExtra(EXTRA_MAQUINA) as Maquina
        val comentarioGeneral = intent.getStringExtra(EXTRA_COMENTARIO_GENERAL) ?: ""
        val hayFallos = intent.getBooleanExtra(EXTRA_HAY_FALLOS, false)

        // CORRECCIÓN: Recuperamos como STRING ya que quieres que se guarden como textos
        val preguntasFallidas = intent.getStringArrayListExtra(EXTRA_PREGUNTAS_FALLIDAS) ?: arrayListOf<String>()

        vistaFirma = findViewById(R.id.vistaFirmaActividad)
        botonLimpiar = findViewById(R.id.botonLimpiarFirma)
        botonEnviar = findViewById(R.id.botonEnviarFirma)
        botonAtras = findViewById(R.id.botonAtrasActividad)
        progressBar = findViewById(R.id.progressBarFirma)

        botonLimpiar.setOnClickListener {
            vistaFirma.limpiar()
            Toast.makeText(this, "Firma limpiada", Toast.LENGTH_SHORT).show()
        }

        botonEnviar.setOnClickListener {
            Log.d(TAG, "  Botón ENVIAR pulsado")
            // CORRECCIÓN LÍNEA 75: Ahora pasamos los 3 argumentos correctamente
            validarYEnviar(comentarioGeneral, hayFallos, preguntasFallidas)
        }

        botonAtras.setOnClickListener {
            finish()
        }
    }

    // CORRECCIÓN: El tipo de la lista es List<String>
    private fun validarYEnviar(comentarioGeneral: String, hayFallos: Boolean, preguntasFallidas: List<String>) {
        if (vistaFirma.estaVacia()) {
            Toast.makeText(this, "Por favor firma antes de enviar", Toast.LENGTH_SHORT).show()
            return
        }
        enviarChecklist(comentarioGeneral, hayFallos, preguntasFallidas)
    }

    // CORRECCIÓN: El tipo de la lista es List<String>
    private fun enviarChecklist(comentarioGeneral: String, hayFallos: Boolean, preguntasFallidas: List<String>) {
        mostrarCargando(true)
        val firma = vistaFirma.obtenerFirmaBase64()
        val fechaHora = Utilidades.obtenerFechaHoraMysql()

        lifecycleScope.launch {
            try {
                // CORRECCIÓN LÍNEA 102: Ahora coincidirá con tu modelo de texto
                val revision = Revision(
                    idUsuario = usuario.id,
                    idMaquina = maquina.id,
                    fechaHora = fechaHora,
                    firma = firma,
                    tieneFallos = hayFallos,
                    comentario = comentarioGeneral,
                    preguntasFallidas = preguntasFallidas
                )

                val revisionCreada = repositorio.crearRevision(revision)

                if (revisionCreada != null) {
                    @Suppress("UNCHECKED_CAST")
                    val resultadosRaw = intent.getSerializableExtra("resultados") as? Map<*, *>

                    if (resultadosRaw != null) {
                        val resultadosMap: Map<Int, Boolean?> = resultadosRaw.entries
                            .mapNotNull { (k, v) ->
                                val idItem = (k as? Int) ?: (k as? Number)?.toInt()
                                val resultado = v as? Boolean
                                if (idItem != null) idItem to resultado else null
                            }
                            .toMap()

                        resultadosMap.forEach { (idItem, resultado) ->
                            if (resultado != null) {
                                val detalle = DetalleRevision(
                                    idRevision = revisionCreada.id,
                                    idItem = idItem,
                                    resultado = resultado
                                )
                                repositorio.crearDetalle(detalle)
                            }
                        }
                    }

                    mostrarCargando(false)
                    com.panelsandwich.checklist.sincronizacion.SincronizadorWifi.sincronizarSiHayWifi(this@ActividadFirma)

                    Toast.makeText(this@ActividadFirma, "Check-list guardado correctamente", Toast.LENGTH_LONG).show()

                    val intentMenu = Intent(this@ActividadFirma, ActividadMenuPrincipal::class.java)
                    intentMenu.putExtra(ActividadUsuarios.EXTRA_USUARIO, usuario)
                    intentMenu.putExtra(ActividadUsuarios.EXTRA_ALMACEN, almacen)
                    intentMenu.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intentMenu)

                } else {
                    mostrarCargando(false)
                    Toast.makeText(this@ActividadFirma, "Error al enviar el check-list.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                mostrarCargando(false)
                Toast.makeText(this@ActividadFirma, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) android.view.View.VISIBLE else android.view.View.GONE
        botonEnviar.isEnabled = !cargando
        botonLimpiar.isEnabled = !cargando
        botonAtras.isEnabled = !cargando
    }
}