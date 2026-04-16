package com.panelsandwich.checklist.actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.basedatos.Repositorio
import com.panelsandwich.checklist.modelos.Almacen
import com.panelsandwich.checklist.modelos.Maquina
import com.panelsandwich.checklist.modelos.Revision
import com.panelsandwich.checklist.modelos.Usuario
import kotlinx.coroutines.launch

class ActividadMaquinaria : AppCompatActivity() {

    private lateinit var contenedorMaquinas: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var textoError: TextView
    private lateinit var botonAtras: Button

    private lateinit var usuario: Usuario
    private lateinit var almacen: Almacen

    private val repositorio = Repositorio()

    companion object {
        const val EXTRA_MAQUINA = "extra_maquina"
        const val EXTRA_CORRIGIENDO_FALLOS = "extra_corrigiendo_fallos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_maquinaria)

        usuario = intent.getSerializableExtra(ActividadUsuarios.EXTRA_USUARIO) as Usuario
        almacen = intent.getSerializableExtra(ActividadUsuarios.EXTRA_ALMACEN) as Almacen

        contenedorMaquinas = findViewById(R.id.contenedorMaquinas)
        progressBar = findViewById(R.id.progressBarMaquinaria)
        textoError = findViewById(R.id.textoErrorMaquinaria)
        botonAtras = findViewById(R.id.botonAtrasMaquinaria)
        botonAtras.setOnClickListener { finish() }

        cargarMaquinas()
    }

    override fun onResume() {
        super.onResume()
        // Recargar al volver de una revisión para actualizar los colores de estado
        cargarMaquinas()
    }

    private fun cargarMaquinas() {
        mostrarCargando(true)
        lifecycleScope.launch {
            try {
                val maquinas = repositorio.obtenerMaquinasPorAlmacen(almacen.id)
                val revisionesHoy = repositorio.obtenerRevisionesDeHoyPorUsuario(usuario.id)
                mostrarCargando(false)
                if (maquinas.isEmpty()) {
                    textoError.visibility = View.VISIBLE
                    textoError.text = "No hay máquinas en este almacén"
                } else {
                    crearBotonesMaquinas(maquinas, revisionesHoy)
                }
            } catch (e: Exception) {
                mostrarCargando(false)
                textoError.visibility = View.VISIBLE
                textoError.text = "Error al cargar máquinas"
            }
        }
    }

    private fun crearBotonesMaquinas(maquinas: List<Maquina>, revisionesHoy: List<Revision>) {
        contenedorMaquinas.removeAllViews()
        val margen = resources.getDimensionPixelSize(R.dimen.margen_boton)
        val alturaBoton = resources.getDimensionPixelSize(R.dimen.altura_boton_principal)
        val padding = resources.getDimensionPixelSize(R.dimen.padding_boton)

        maquinas.forEach { maquina ->
            val revisionHoy   = revisionesHoy.firstOrNull { it.idMaquina == maquina.id }
            val revisadaOk    = revisionHoy != null && !revisionHoy.tieneFallos  // verde, bloqueada
            val revisadaFallo = revisionHoy != null && revisionHoy.tieneFallos   // rojo, permite corregir

            val boton = Button(this).apply {
                text = when {
                    revisadaOk    -> "✓ ${maquina.nombre.uppercase()}"
                    revisadaFallo -> "⚠ ${maquina.nombre.uppercase()}"
                    else          -> maquina.nombre.uppercase()
                }
                setTextColor(ContextCompat.getColor(context, R.color.texto_blanco))
                backgroundTintList = when {
                    revisadaOk    -> ContextCompat.getColorStateList(context, R.color.boton_verde)
                    revisadaFallo -> ContextCompat.getColorStateList(context, R.color.boton_rojo)
                    else          -> ContextCompat.getColorStateList(context, R.color.boton_azul)
                }
                textSize = 20f
                isAllCaps = false
                setPadding(padding, padding, padding, padding)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, alturaBoton)
                params.setMargins(margen, margen, margen, margen)
                layoutParams = params

                setOnClickListener {
                    when {
                        revisadaOk -> {
                            // Completada sin fallos → bloqueada hasta el reinicio de las 05:00
                            Toast.makeText(
                                this@ActividadMaquinaria,
                                "✓ ${maquina.nombre} ya fue revisada hoy sin fallos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        revisadaFallo -> {
                            // Tiene fallos → bloqueada, no se puede modificar
                            Toast.makeText(
                                this@ActividadMaquinaria,
                                "⚠ ${maquina.nombre} ya fue revisada hoy con fallos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // Sin revisar → acceso normal
                            val intent = Intent(this@ActividadMaquinaria, ActividadCheckMaquina::class.java)
                            intent.putExtra(ActividadUsuarios.EXTRA_USUARIO, usuario)
                            intent.putExtra(ActividadUsuarios.EXTRA_ALMACEN, almacen)
                            intent.putExtra(EXTRA_MAQUINA, maquina)
                            intent.putExtra(EXTRA_CORRIGIENDO_FALLOS, false)
                            startActivity(intent)
                        }
                    }
                }
            }
            contenedorMaquinas.addView(boton)
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        contenedorMaquinas.visibility = if (cargando) View.GONE else View.VISIBLE
    }
}
