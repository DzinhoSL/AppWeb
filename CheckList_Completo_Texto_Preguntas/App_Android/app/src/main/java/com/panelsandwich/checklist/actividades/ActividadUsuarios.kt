package com.panelsandwich.checklist.actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.panelsandwich.checklist.modelos.Usuario
import com.panelsandwich.checklist.utilidades.SesionManager
import kotlinx.coroutines.launch

/**
 * **ActividadUsuarios**: Esta actividad permite al usuario seleccionar su identidad
 * dentro de un almacén previamente elegido. Es un paso intermedio crucial para
 * personalizar la experiencia del usuario y asociar las revisiones a la persona correcta.
 *
 * La interfaz de usuario presenta:
 * - El nombre del almacén seleccionado en la parte superior.
 * - Una cuadrícula de botones, donde cada botón representa un usuario disponible en ese almacén.
 * - Un botón para volver a la pantalla anterior (selección de almacén).
 * También gestiona la carga de datos de los usuarios desde un repositorio y muestra un indicador
 * de progreso o un mensaje de error si la carga falla o no hay usuarios.
 */
class ActividadUsuarios : AppCompatActivity() {

    // declaración de las vistas principales de la actividad.
    private lateinit var tituloAlmacen: TextView
    private lateinit var recyclerUsuarios: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textoError: TextView
    private lateinit var botonAtras: Button

    // el almacén que fue seleccionado en la actividad anterior.
    private lateinit var almacenSeleccionado: Almacen

    // instancia del Repositorio para interactuar con la capa de datos.
    private val repositorio = Repositorio()

    /**
     * Objeto `companion` que contiene constantes compartidas por la clase.
     * Define claves para pasar los objetos `Usuario` y `Almacen` entre actividades usando `Intent`.
     */
    companion object {
        const val EXTRA_USUARIO = "extra_usuario"
        const val EXTRA_ALMACEN = "extra_almacen"
    }

    /**
     * Método `onCreate`: Se llama cuando la actividad se crea por primera vez.
     * Aquí se realiza la inicialización de la interfaz de usuario, se recupera el almacén
     * seleccionado, se actualizan los textos y se configuran los listeners de los botones.
     *
     * @param savedInstanceState Si la actividad se recrea (por ejemplo, después de un cambio de orientación),
     *                           este Bundle contiene los datos que se guardaron previamente en `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // establece el layout de la actividad desde el archivo XML `actividad_usuarios.xml`.
        setContentView(R.layout.actividad_usuarios)

        // recupera el objeto Almacen que fue pasado a esta actividad a través del Intent.
        // Este almacén es fundamental para filtrar los usuarios que pertenecen a él.
        almacenSeleccionado = intent.getSerializableExtra(ActividadAlmacenes.EXTRA_ALMACEN) as Almacen

        // vincula las variables con los elementos de la interfaz de usuario definidos en el layout.
        tituloAlmacen = findViewById(R.id.tituloAlmacenUsuarios)
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios)
        progressBar = findViewById(R.id.progressBarUsuarios)
        textoError = findViewById(R.id.textoErrorUsuarios)
        botonAtras = findViewById(R.id.botonAtrasUsuarios)

        // establece el título de la actividad con el nombre del almacén en mayúsculas.
        tituloAlmacen.text = almacenSeleccionado.nombre.uppercase()
        // Configura el RecyclerView para que muestre los elementos en una cuadrícula de 2 columnas.
        recyclerUsuarios.layoutManager = GridLayoutManager(this, 2)

        // configura el listener para el botón "ATRÁS".
        // al pulsarlo, finaliza esta actividad y regresa a la actividad anterior en la pila.
        botonAtras.setOnClickListener { finish() }

        // inicia el proceso de carga de los usuarios para el almacén seleccionado.
        cargarUsuarios()
    }

    /**
     * `cargarUsuarios`: Este método asíncrono se encarga de obtener la lista de usuarios
     * asociados al almacén actual desde el repositorio. Muestra un indicador de carga mientras
     * espera los datos y, una vez recibidos, configura el adaptador del RecyclerView.
     */
    private fun cargarUsuarios() {
        // muestra el ProgressBar para indicar que la carga está en curso.
        mostrarCargando(true)
        // inicia una corrutina para realizar la operación de red/base de datos de forma segura.
        lifecycleScope.launch {
            try {
                // intenta obtener la lista de usuarios usando el ID del almacén seleccionado.
                val usuarios = repositorio.obtenerUsuariosPorAlmacen(almacenSeleccionado.id)
                // oculta el ProgressBar una vez que la operación ha finalizado.
                mostrarCargando(false)
                // comprueba si se han encontrado usuarios.
                if (usuarios.isEmpty()) {
                    // si no hay usuarios, muestra un mensaje de error al usuario.
                    textoError.visibility = View.VISIBLE
                    textoError.text = "No hay usuarios en este almacén"
                } else {
                    // si hay usuarios, configura el adaptador del RecyclerView con los datos obtenidos.
                    configurarAdaptador(usuarios)
                }
            } catch (e: Exception) {
                // en caso de error, oculta el ProgressBar y muestra un mensaje de error al usuario.
                mostrarCargando(false)
                textoError.visibility = View.VISIBLE
                textoError.text = "Error al cargar usuarios"
            }
        }
    }

    /**
     * `configurarAdaptador`: Este método toma una lista de objetos `Usuario` y los prepara
     * para ser mostrados en el `RecyclerView` utilizando un `AdaptadorBotonGrid`.
     * También define la acción a realizar cuando el usuario pulsa sobre un botón de usuario,
     * que incluye guardar la sesión y navegar al menú principal.
     *
     * @param usuarios La lista de objetos `Usuario` a mostrar.
     */
    private fun configurarAdaptador(usuarios: List<Usuario>) {
        // mapea la lista de objetos Usuario a una lista de pares (ID, Nombre) para el adaptador.
        val elementos = usuarios.map { Pair(it.id, it.nombre) }
        // crea una instancia del AdaptadorBotonGrid, pasándole los elementos y una función lambda
        // que se ejecutará cuando se haga clic en un botón de usuario.
        val adaptador = AdaptadorBotonGrid(elementos) { id, nombre ->
            // busca el objeto Usuario completo a partir del ID seleccionado.
            val usuario = usuarios.first { it.id == id }
            // guarda la información de la sesión (usuario y almacén) utilizando el SesionManager.
            SesionManager.guardarSesion(this, usuario, almacenSeleccionado)

            // crea un Intent para iniciar la ActividadMenuPrincipal.
            val intent = Intent(this, ActividadMenuPrincipal::class.java)
            // pasa el objeto Usuario y el Almacen seleccionado a la siguiente actividad.
            intent.putExtra(EXTRA_USUARIO, usuario)
            intent.putExtra(EXTRA_ALMACEN, almacenSeleccionado)
            // inicia la ActividadMenuPrincipal.
            startActivity(intent)
        }
        // asigna el adaptador configurado al RecyclerView.
        recyclerUsuarios.adapter = adaptador
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
        recyclerUsuarios.visibility = if (cargando) View.GONE else View.VISIBLE
    }
}
