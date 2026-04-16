package com.panelsandwich.checklist.actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.adaptadores.AdaptadorBotonGrid
import com.panelsandwich.checklist.basedatos.Repositorio
import com.panelsandwich.checklist.modelos.Almacen
import kotlinx.coroutines.launch

/**
 * **ActividadAlmacenes**: Esta es la primera pantalla que ve el usuario al iniciar la aplicación.
 * Su propósito principal es permitir al usuario seleccionar un almacén específico de una lista.
 * Una vez seleccionado, la aplicación pasará a la siguiente etapa, que es la selección de usuarios.
 *
 * Muestra una cuadrícula de botones, donde cada botón representa un almacén disponible.
 * Utiliza un `RecyclerView` con un `GridLayoutManager` para organizar estos botones de forma visualmente atractiva.
 * También gestiona la carga de datos de los almacenes desde un repositorio y muestra un indicador de progreso
 * o un mensaje de error si la carga falla o no hay almacenes.
 */
class ActividadAlmacenes : AppCompatActivity() {

    // declaración de las vistas que se utilizan en esta actividad.
    // `lateinit` indica que estas variables se inicializarán más tarde, antes de ser usadas.
    private lateinit var recyclerAlmacenes: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textoError: TextView

    // instancia del Repositorio para interactuar con la fuente de datos (API, base de datos).
    private val repositorio = Repositorio()

    /**
     * Objeto `companion` que contiene constantes compartidas por la clase.
     * En este caso, define una clave para pasar el objeto `Almacen` entre actividades usando `Intent`.
     */
    companion object {
        const val EXTRA_ALMACEN = "extra_almacen"
    }

    /**
     * Método `onCreate`: Se llama cuando la actividad se crea por primera vez.
     * Aquí se realiza la inicialización básica de la actividad, como la configuración del layout
     * y la vinculación de las vistas con sus IDs en el XML.
     *
     * @param savedInstanceState Si la actividad se recrea (por ejemplo, después de un cambio de orientación),
     *                           este Bundle contiene los datos que se guardaron previamente en `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // establece el layout de la actividad desde el archivo XML `actividad_almacenes.xml`.
        setContentView(R.layout.actividad_almacenes)

        // vincula las variables con los elementos de la interfaz de usuario definidos en el layout.
        recyclerAlmacenes = findViewById(R.id.recyclerAlmacenes)
        progressBar = findViewById(R.id.progressBarAlmacenes)
        textoError = findViewById(R.id.textoErrorAlmacenes)

        // configura el `RecyclerView` para que muestre los elementos en una cuadrícula de 2 columnas.
        recyclerAlmacenes.layoutManager = GridLayoutManager(this, 2)

        // inicia el proceso de carga de los almacenes desde el repositorio.
        cargarAlmacenes()
    }

    /**
     * `cargarAlmacenes`: Este método se encarga de obtener la lista de almacenes del repositorio.
     * Muestra un indicador de carga mientras se obtienen los datos y, una vez completado,
     * actualiza la interfaz de usuario con los almacenes o un mensaje de error.
     */
    private fun cargarAlmacenes() {
        // muestra el ProgressBar y oculta el RecyclerView para indicar que se están cargando datos.
        mostrarCargando(true)
        // inicia una corrutina en el ámbito del ciclo de vida de la actividad para realizar operaciones asíncronas.
        lifecycleScope.launch {
            try {
                // intenta obtener la lista de almacenes del repositorio.
                val almacenes = repositorio.obtenerAlmacenes()
                // oculta el ProgressBar una vez que los datos han sido obtenidos (o ha ocurrido un error).
                mostrarCargando(false)
                // verifica si la lista de almacenes está vacía.
                if (almacenes.isEmpty()) {
                    // si no hay almacenes, muestra un mensaje de error al usuario.
                    textoError.visibility = View.VISIBLE
                    textoError.text = "No hay almacenes disponibles"
                } else {
                    // si hay almacenes, configura el adaptador del RecyclerView con los datos obtenidos.
                    configurarAdaptador(almacenes)
                }
            } catch (e: Exception) {
                // si ocurre un error durante la carga, oculta el ProgressBar y muestra un mensaje de error.
                mostrarCargando(false)
                textoError.visibility = View.VISIBLE
                textoError.text = "Error al cargar almacenes"
            }
        }
    }

    /**
     * `configurarAdaptador`: Este método toma una lista de objetos `Almacen` y los prepara
     * para ser mostrados en el `RecyclerView` utilizando un `AdaptadorBotonGrid`.
     * También define la acción a realizar cuando el usuario pulsa sobre un botón de almacén.
     *
     * @param almacenes La lista de objetos `Almacen` a mostrar.
     */
    private fun configurarAdaptador(almacenes: List<Almacen>) {
        // mapea la lista de objetos Almacen a una lista de pares (ID, Nombre) para el adaptador.
        val elementos = almacenes.map { Pair(it.id, it.nombre) }
        // crea una instancia del AdaptadorBotonGrid, pasándole los elementos y una función lambda
        // que se ejecutará cuando se haga clic en un botón.
        val adaptador = AdaptadorBotonGrid(elementos) { id, nombre ->
            // Cuando se pulsa un almacén, crea un objeto Almacen con el ID y nombre seleccionados.
            val almacen = Almacen(id, nombre)
            // crea un Intent para iniciar la ActividadUsuarios.
            val intent = Intent(this, ActividadUsuarios::class.java)
            // pasa el objeto Almacen a la siguiente actividad.
            intent.putExtra(EXTRA_ALMACEN, almacen)
            // inicia la ActividadUsuarios.
            startActivity(intent)
        }
        // asigna el adaptador configurado al RecyclerView.
        recyclerAlmacenes.adapter = adaptador
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
        recyclerAlmacenes.visibility = if (cargando) View.GONE else View.VISIBLE
    }
}
