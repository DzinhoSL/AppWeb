package com.panelsandwich.checklist.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.adaptadores.AdaptadorItemsChecklist
import com.panelsandwich.checklist.basedatos.Repositorio
import com.panelsandwich.checklist.modelos.*
import com.panelsandwich.checklist.utilidades.Utilidades
import kotlinx.coroutines.launch

/**
 * **ActividadCheckMaquina**: Esta actividad es el corazón del proceso de revisión de una máquina.
 * Aquí es donde el usuario interactúa con el checklist, respondiendo a cada ítem con un SÍ o un NO.
 * Si se detectan fallos (algún NO), se le pide al usuario que añada un comentario general.
 * Una vez completado el checklist, el botón "SIGUIENTE" le llevará a la pantalla de firma para finalizar el proceso.
 *
 * Se encarga de:
 * - Mostrar el nombre de la máquina que se está revisando.
 * - Presentar una lista dinámica de ítems del checklist para esa máquina.
 * - Gestionar la visibilidad del campo de comentario general si hay fallos.
 * - Validar que todos los ítems estén respondidos y que el comentario sea obligatorio si aplica.
 * - Navegar a la `ActividadFirma` pasando todos los datos necesarios para el envío final.
 */
private const val TAG_CHECK = "DEBUG_CHECKMAQUINA"

class ActividadCheckMaquina : AppCompatActivity() {

    // declaración de las vistas principales de la actividad.
    private lateinit var tituloMaquina: TextView
    private lateinit var recyclerItems: RecyclerView
    private lateinit var scrollViewItems: NestedScrollView
    private lateinit var contenedorComentarioGeneral: View
    private lateinit var campoComentarioGeneral: EditText
    private lateinit var botonEnviar: Button // este botón ahora se llama "SIGUIENTE"
    private lateinit var botonMenu: Button
    private lateinit var progressBar: ProgressBar

    // adaptador para gestionar la lista de ítems del checklist en el RecyclerView.
    private lateinit var adaptador: AdaptadorItemsChecklist

    // datos de la sesión actual, pasados desde actividades anteriores.
    private lateinit var usuario: Usuario
    private lateinit var almacen: Almacen
    private lateinit var maquina: Maquina

    // instancia del Repositorio para interactuar con la capa de datos.
    private val repositorio = Repositorio()

    /**
     * Método `onCreate`: Se llama cuando la actividad se crea por primera vez.
     * Aquí se inicializan las vistas, se recuperan los datos pasados de otras actividades
     * y se configuran los listeners para los botones.
     *
     * @param savedInstanceState Si la actividad se recrea (por ejemplo, después de un cambio de orientación),
     *                           este Bundle contiene los datos que se guardaron previamente en `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // establece el layout de la actividad desde el archivo XML `actividad_check_maquina.xml`.
        setContentView(R.layout.actividad_check_maquina)

        // recupera los objetos Usuario, Almacen y Maquina que fueron pasados a esta actividad
        // a través del Intent. Estos son cruciales para el contexto del checklist.
        usuario = intent.getSerializableExtra(ActividadUsuarios.EXTRA_USUARIO) as Usuario
        almacen = intent.getSerializableExtra(ActividadUsuarios.EXTRA_ALMACEN) as Almacen
        maquina = intent.getSerializableExtra(ActividadMaquinaria.EXTRA_MAQUINA) as Maquina

        // vincula las variables con los elementos de la interfaz de usuario definidos en el layout.
        tituloMaquina = findViewById(R.id.tituloMaquinaCheck)
        recyclerItems = findViewById(R.id.recyclerItemsChecklist)
        scrollViewItems = findViewById(R.id.scrollViewItems)
        contenedorComentarioGeneral = findViewById(R.id.contenedorComentarioGeneral)
        campoComentarioGeneral = findViewById(R.id.campoComentarioGeneral)
        botonEnviar = findViewById(R.id.botonEnviarCheck)
        botonMenu = findViewById(R.id.botonMenuCheck)
        progressBar = findViewById(R.id.progressBarCheck)

        // establece el título de la actividad con el nombre de la máquina en mayúsculas.
        tituloMaquina.text = maquina.nombre.uppercase()
        // configura el RecyclerView para usar un LinearLayoutManager, mostrando los ítems en una lista vertical.
        recyclerItems.layoutManager = LinearLayoutManager(this)

        // configura el listener para el botón "SIGUIENTE". Cuando se pulsa, se llama a `validarYSiguiente()`.
        botonEnviar.setOnClickListener { validarYSiguiente() }

        // configura el listener para el botón "MENU" (Atrás).
        // al pulsarlo, se navega de vuelta a la `ActividadMenuPrincipal`.
        botonMenu.setOnClickListener {
            val intent = Intent(this, ActividadMenuPrincipal::class.java)
            intent.putExtra(ActividadUsuarios.EXTRA_USUARIO, usuario)
            intent.putExtra(ActividadUsuarios.EXTRA_ALMACEN, almacen)
            // esta bandera asegura que todas las actividades por encima de la principal se cierren.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        // inicia la carga de los ítems del checklist para la máquina seleccionada.
        cargarItemsChecklist()
    }

    /**
     * `cargarItemsChecklist`: Este método asíncrono se encarga de obtener los ítems del checklist
     * para la máquina actual desde el repositorio. Muestra un indicador de carga mientras espera
     * los datos y, una vez recibidos, configura el adaptador del RecyclerView.
     */
    private fun cargarItemsChecklist() {
        Log.d(TAG_CHECK, "▶ cargarItemsChecklist() — idMaquina=${maquina.id}")
        mostrarCargando(true)
        lifecycleScope.launch {
            try {
                val items = repositorio.obtenerItemsPorMaquina(maquina.id)
                mostrarCargando(false)
                if (items.isEmpty()) {
                    Log.w(TAG_CHECK, "⚠ cargarItemsChecklist() — lista vacía")
                    Toast.makeText(this@ActividadCheckMaquina, "No hay ítems para esta máquina", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG_CHECK, "✅ cargarItemsChecklist() — ${items.size} ítems cargados")
                    adaptador = AdaptadorItemsChecklist(items) {
                        actualizarVisibilidadComentario()
                    }
                    // Scroll callback: localiza la vista del ítem siguiente en el RecyclerView
                    // y desplaza el NestedScrollView para que quede visible
                    adaptador.alScrollSiguiente = { posicion ->
                        recyclerItems.post {
                            val layoutManager = recyclerItems.layoutManager as? LinearLayoutManager
                            val itemView = layoutManager?.findViewByPosition(posicion)
                            if (itemView != null) {
                                // El ítem ya está en pantalla: scroll suave al offset de esa vista
                                val offsetTop = itemView.top + recyclerItems.top
                                scrollViewItems.smoothScrollTo(0, offsetTop)
                            } else {
                                // El ítem no está en pantalla aún: scroll al final para revelarlo
                                scrollViewItems.post {
                                    scrollViewItems.smoothScrollBy(0, 600)
                                }
                            }
                        }
                    }
                    recyclerItems.adapter = adaptador
                }
            } catch (e: Exception) {
                Log.e(TAG_CHECK, "❌ cargarItemsChecklist() EXCEPCIÓN: ${e.javaClass.simpleName}: ${e.message}")
                mostrarCargando(false)
                Toast.makeText(this@ActividadCheckMaquina, "Error al cargar el checklist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * `actualizarVisibilidadComentario`: Este método se llama cada vez que un ítem del checklist
     * cambia de estado (SÍ/NO). Su función es mostrar u ocultar el contenedor del comentario general
     * en función de si hay algún fallo (`NO`) registrado en el checklist.
     */
    private fun actualizarVisibilidadComentario() {
        // si el adaptador indica que hay al menos un fallo (un ítem marcado como NO).
        if (adaptador.hayFallos()) {
            // hace visible el contenedor del comentario general.
            contenedorComentarioGeneral.visibility = View.VISIBLE
        } else {
            // si no hay fallos, oculta el contenedor del comentario general.
            contenedorComentarioGeneral.visibility = View.GONE
            // además, limpia el texto del campo de comentario para evitar datos residuales.
            campoComentarioGeneral.setText("")
        }
    }

    /**
     * `ocultarTeclado`: Un método de utilidad para ocultar el teclado virtual del dispositivo.
     * Esto mejora la experiencia de usuario, especialmente después de interactuar con campos de texto.
     */
    private fun ocultarTeclado() {
        // obtiene una instancia del InputMethodManager, que controla el teclado virtual.
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // solicita al sistema que oculte el teclado, usando el token de la ventana del campo de comentario.
        imm.hideSoftInputFromWindow(campoComentarioGeneral.windowToken, 0)
    }

    /**
     * `validarYSiguiente`: Este método se ejecuta cuando el usuario pulsa el botón "SIGUIENTE".
     * Realiza varias validaciones para asegurar que el checklist esté completo y correcto antes
     * de pasar a la pantalla de firma. Si alguna validación falla, muestra un mensaje al usuario.
     */
    private fun validarYSiguiente() {
        Log.d(TAG_CHECK, "▶ validarYSiguiente() pulsado")
        if (!::adaptador.isInitialized) {
            Log.w(TAG_CHECK, "⚠ adaptador no inicializado, abortando")
            return
        }

        val todosRespondidos = adaptador.todosRespondidos()
        val hayFallos = adaptador.hayFallos()
        Log.d(TAG_CHECK, "  todosRespondidos=$todosRespondidos, hayFallos=$hayFallos")

        if (!todosRespondidos) {
            Log.w(TAG_CHECK, "⚠ No todos los ítems respondidos")
            Toast.makeText(this, "Por favor responde todos los ítems del checklist", Toast.LENGTH_LONG).show()
            return
        }

        val comentarioGeneral = campoComentarioGeneral.text.toString().trim()
        if (hayFallos && comentarioGeneral.isEmpty()) {
            Log.w(TAG_CHECK, "⚠ Hay fallos pero el comentario está vacío")
            Toast.makeText(this, "Debes describir el problema en el campo de comentario general", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(TAG_CHECK, "✅ Validaciones OK — yendo a pantalla de firma")
        irAPantallaFirma(comentarioGeneral)
    }

    /**
     * `irAPantallaFirma`: Este método se encarga de iniciar la `ActividadFirma`.
     * Pasa todos los datos relevantes del checklist (usuario, almacén, máquina, resultados de los ítems,
     * comentario general y si hay fallos) a la siguiente actividad para que puedan ser utilizados
     * en el proceso de firma y envío final.
     *
     * @param comentarioGeneral El texto del comentario general introducido por el usuario.
     */
    private fun irAPantallaFirma(comentarioGeneral: String) {
        val resultados = adaptador.obtenerResultados()
        val textosPreguntasFallidas = adaptador.obtenerTextosFallidos()
        Log.d(TAG_CHECK, "▶ irAPantallaFirma() — comentario='$comentarioGeneral', hayFallos=${adaptador.hayFallos()}, ${resultados.size} resultados: $resultados")
        val intent = Intent(this, ActividadFirma::class.java)
        intent.putExtra(ActividadFirma.EXTRA_USUARIO, usuario)
        intent.putExtra(ActividadFirma.EXTRA_ALMACEN, almacen)
        intent.putExtra(ActividadFirma.EXTRA_MAQUINA, maquina)
        intent.putExtra(ActividadFirma.EXTRA_COMENTARIO_GENERAL, comentarioGeneral)
        intent.putExtra(ActividadFirma.EXTRA_HAY_FALLOS, adaptador.hayFallos())
        intent.putExtra("resultados", HashMap(resultados))
        intent.putExtra(ActividadFirma.EXTRA_PREGUNTAS_FALLIDAS, ArrayList(textosPreguntasFallidas))
        Log.d(TAG_CHECK, "✅ Iniciando ActividadFirma")
        startActivity(intent)
    }

    /**
     * `mostrarCargando`: Controla la visibilidad del `ProgressBar` y la habilitación del botón "SIGUIENTE".
     * Cuando `cargando` es `true`, muestra el `ProgressBar` y deshabilita el botón.
     * Cuando `cargando` es `false`, oculta el `ProgressBar` y habilita el botón.
     *
     * @param cargando Un booleano que indica si se debe mostrar el estado de carga.
     */
    private fun mostrarCargando(cargando: Boolean) {
        // alterna la visibilidad del ProgressBar.
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        // habilita o deshabilita el botón "SIGUIENTE" para evitar interacciones durante la carga.
        botonEnviar.isEnabled = !cargando
    }
}
