package com.panelsandwich.checklist.actividades

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.adaptadores.AdaptadorRegistroMaquinas
import com.panelsandwich.checklist.basedatos.Repositorio
import com.panelsandwich.checklist.modelos.*
import com.panelsandwich.checklist.utilidades.Utilidades
import kotlinx.coroutines.launch

/**
 * **ActividadRegistro**: Esta actividad se encarga de mostrar un historial o registro
 * de los checklists que el usuario ha realizado. Su objetivo es proporcionar una visión
 * rápida del estado de las máquinas en el almacén para el día actual, indicando si
 * una máquina ha sido revisada y si se detectaron fallos.
 *
 * La interfaz de usuario presenta:
 * - El nombre del almacén y las iniciales del usuario.
 * - La fecha y hora actual.
 * - Una lista de máquinas con su estado de revisión (por ejemplo, un indicador visual de si tiene fallos).
 * - Un botón para volver a la pantalla anterior.
 * También gestiona la carga de datos de las revisiones desde un repositorio y muestra un indicador
 * de progreso o un mensaje si no hay registros disponibles.
 */
class ActividadRegistro : AppCompatActivity() {

    // declaración de las vistas principales de la actividad.
    private lateinit var tituloRegistro: TextView
    private lateinit var inicialesUsuario: TextView
    private lateinit var textoFechaHora: TextView
    private lateinit var recyclerRegistro: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textoVacio: TextView
    private lateinit var botonAtras: Button

    // datos de la sesión actual, pasados desde actividades anteriores.
    private lateinit var usuario: Usuario
    private lateinit var almacen: Almacen

    // instancia del Repositorio para interactuar con la capa de datos.
    private val repositorio = Repositorio()

    /**
     * Método `onCreate`: Se llama cuando la actividad se crea por primera vez.
     * Aquí se inicializan las vistas, se recuperan los datos del usuario y almacén,
     * se actualizan los textos y se configuran los listeners de los botones.
     *
     * @param savedInstanceState Si la actividad se recrea (por ejemplo, después de un cambio de orientación),
     *                           este Bundle contiene los datos que se guardaron previamente en `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // establece el layout de la actividad desde el archivo XML `actividad_registro.xml`.
        setContentView(R.layout.actividad_registro)

        // recupera los objetos Usuario y Almacen que fueron pasados a esta actividad
        // a través del Intent. Estos datos son esenciales para filtrar los registros correctos.
        usuario = intent.getSerializableExtra(ActividadUsuarios.EXTRA_USUARIO) as Usuario
        almacen = intent.getSerializableExtra(ActividadUsuarios.EXTRA_ALMACEN) as Almacen

        // vincula las variables con los elementos de la interfaz de usuario definidos en el layout.
        tituloRegistro = findViewById(R.id.tituloRegistro)
        inicialesUsuario = findViewById(R.id.inicialesUsuarioRegistro)
        textoFechaHora = findViewById(R.id.textoFechaHoraRegistro)
        recyclerRegistro = findViewById(R.id.recyclerRegistro)
        progressBar = findViewById(R.id.progressBarRegistro)
        textoVacio = findViewById(R.id.textoVacioRegistro)
        botonAtras = findViewById(R.id.botonAtrasRegistro)

        // establece el título del registro, las iniciales del usuario y la fecha/hora actual.
        tituloRegistro.text = getString(R.string.titulo_registro)
        inicialesUsuario.text = usuario.obtenerIniciales()
        textoFechaHora.text = Utilidades.obtenerFechaHoraActual()
        // configura el RecyclerView para usar un LinearLayoutManager, mostrando los ítems en una lista vertical.
        recyclerRegistro.layoutManager = LinearLayoutManager(this)

        // configura el listener para el botón "ATRÁS".
        // al pulsarlo, finaliza esta actividad y regresa a la actividad anterior en la pila.
        botonAtras.setOnClickListener { finish() }

        // inicia la carga de los registros de checklist.
        cargarRegistros()
    }

    /**
     * `cargarRegistros`: Este método asíncrono se encarga de obtener las máquinas del almacén
     * y las revisiones realizadas por el usuario para el día actual. Luego, combina esta información
     * para mostrar el estado de cada máquina en el `RecyclerView`.
     */
    private fun cargarRegistros() {
        // muestra el ProgressBar para indicar que la carga está en curso.
        mostrarCargando(true)
        // inicia una corrutina para realizar las operaciones de red/base de datos de forma segura.
        lifecycleScope.launch {
            try {
                // obtiene todas las máquinas del almacén actual.
                val maquinas = repositorio.obtenerMaquinasPorAlmacen(almacen.id)
                // obtiene solo las revisiones de HOY del usuario actual (reinicio a las 05:00)
                val revisiones = repositorio.obtenerRevisionesDeHoyPorUsuario(usuario.id)

                // procesa cada máquina para determinar su estado de revisión.
                val estados = maquinas.map { maquina ->
                    // busca si existe una revisión para esta máquina por parte del usuario actual.
                    val revisionMaquina = revisiones.firstOrNull { it.idMaquina == maquina.id }
                    // si hay una revisión, obtiene los detalles asociados a esa revisión.
                    val detalles = if (revisionMaquina != null) {
                        repositorio.obtenerDetallesPorRevision(revisionMaquina.id)
                    } else emptyList()

                    // crea un objeto `EstadoRevisionMaquina` que encapsula la máquina, si tiene fallos,
                    // el comentario general y los detalles de la revisión.
                    EstadoRevisionMaquina(
                        maquina = maquina,
                        tieneFallos = revisionMaquina?.tieneFallos,
                        comentario = revisionMaquina?.comentario ?: "",
                        revision = revisionMaquina,
                        detalles = detalles
                    )
                }

                // oculta el ProgressBar una vez que la operación ha finalizado.
                mostrarCargando(false)

                // comprueba si se han encontrado estados de revisión.
                if (estados.isEmpty()) {
                    // si no hay registros, muestra un mensaje al usuario.
                    textoVacio.visibility = View.VISIBLE
                    textoVacio.text = "No hay registros disponibles"
                } else {
                    // si hay registros, inicializa el adaptador con ellos.
                    val adaptador = AdaptadorRegistroMaquinas(estados) { estado ->
                        // este bloque se ejecutaría si se quisiera añadir una acción al hacer clic
                        // en un elemento del registro (por ejemplo, ver el detalle completo de la revisión).
                        // por ahora, no realiza ninguna acción adicional.
                    }
                    // asigna el adaptador al RecyclerView para mostrar los registros.
                    recyclerRegistro.adapter = adaptador
                }
            } catch (e: Exception) {
                // en caso de error, oculta el ProgressBar y muestra un mensaje de error al usuario.
                mostrarCargando(false)
                textoVacio.visibility = View.VISIBLE
                textoVacio.text = "Error al cargar registros"
            }
        }
    }

    /**
     * `mostrarCargando`: Controla la visibilidad del `ProgressBar` y del `RecyclerView`.
     * Cuando `cargando` es `true`, muestra el `ProgressBar` y oculta el `RecyclerView`.
     * Cuando `cargando` es `false`, oculta el `ProgressBar` y muestra el `RecyclerView`.
     *
     * @param cargando Un booleano que indica si se debe mostrar el estado de carga.
     */
    private fun mostrarCargando(cargando: Boolean) {
        // alterna la visibilidad del ProgressBar.
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        // alterna la visibilidad del RecyclerView.
        recyclerRegistro.visibility = if (cargando) View.GONE else View.VISIBLE
    }
}
