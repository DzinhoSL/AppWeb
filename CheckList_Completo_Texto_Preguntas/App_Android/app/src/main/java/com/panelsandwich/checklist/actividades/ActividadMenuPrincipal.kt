package com.panelsandwich.checklist.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.panelsandwich.checklist.R
import com.panelsandwich.checklist.modelos.Almacen
import com.panelsandwich.checklist.modelos.Usuario
import com.panelsandwich.checklist.utilidades.Utilidades

/**
 * **ActividadMenuPrincipal**: Esta actividad actúa como el centro de navegación principal
 * para el usuario una vez que ha seleccionado un almacén y se ha identificado.
 * Muestra información relevante de la sesión actual y ofrece opciones para iniciar
 * un nuevo checklist o revisar los checklists ya realizados.
 *
 * La interfaz incluye:
 * - El nombre del almacén seleccionado.
 * - Las iniciales del usuario logueado.
 * - La fecha y hora actual.
 * - Un botón para iniciar un "Nuevo Check" (que lleva a la selección de maquinaria).
 * - Un botón para ver "Mis Checks" (que lleva al registro de revisiones).
 * - Un botón para volver a la pantalla anterior (selección de usuario).
 */
class ActividadMenuPrincipal : AppCompatActivity() {

    // declaración de las vistas principales de la actividad.
    private lateinit var tituloAlmacen: TextView
    private lateinit var inicialesUsuario: TextView
    private lateinit var textoFechaHora: TextView
    private lateinit var botonNuevoCheck: Button
    private lateinit var botonMisChecks: Button
    private lateinit var botonAtras: Button

    // datos de la sesión actual, pasados desde actividades anteriores.
    private lateinit var usuario: Usuario
    private lateinit var almacen: Almacen

    /**
     * Método `onCreate`: Se llama cuando la actividad se crea por primera vez.
     * Aquí se realiza la inicialización de la interfaz de usuario, se recuperan los datos
     * del usuario y almacén, se actualizan los textos y se configuran los listeners de los botones.
     *
     * @param savedInstanceState Si la actividad se recrea (por ejemplo, después de un cambio de orientación),
     *                           este Bundle contiene los datos que se guardaron previamente en `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // establece el layout de la actividad desde el archivo XML `actividad_menu_principal.xml`.
        setContentView(R.layout.actividad_menu_principal)

        // recupera los objetos Usuario y Almacen que fueron pasados a esta actividad
        // a través del Intent. Estos datos son esenciales para personalizar el menú.
        usuario = intent.getSerializableExtra(ActividadUsuarios.EXTRA_USUARIO) as Usuario
        almacen = intent.getSerializableExtra(ActividadUsuarios.EXTRA_ALMACEN) as Almacen

        // vincula las variables con los elementos de la interfaz de usuario definidos en el layout.
        tituloAlmacen = findViewById(R.id.tituloAlmacenMenu)
        inicialesUsuario = findViewById(R.id.inicialesUsuarioMenu)
        textoFechaHora = findViewById(R.id.textoFechaHoraMenu)
        botonNuevoCheck = findViewById(R.id.botonNuevoCheck)
        botonMisChecks = findViewById(R.id.botonMisChecks)
        botonAtras = findViewById(R.id.botonAtrasMenu)

        // actualiza los TextViews con la información del almacén, las iniciales del usuario
        // y la fecha/hora actual, formateada por la clase de utilidades.
        tituloAlmacen.text = almacen.nombre.uppercase()
        inicialesUsuario.text = usuario.obtenerIniciales()
        textoFechaHora.text = Utilidades.obtenerFechaHoraActual()

        // configura el listener para el botón "Nuevo Check".
        // al pulsarlo, se inicia la `ActividadMaquinaria` para seleccionar una máquina.
        botonNuevoCheck.setOnClickListener {
            val intent = Intent(this, ActividadMaquinaria::class.java)
            // pasa el usuario y el almacén a la siguiente actividad.
            intent.putExtra(ActividadUsuarios.EXTRA_USUARIO, usuario)
            intent.putExtra(ActividadUsuarios.EXTRA_ALMACEN, almacen)
            startActivity(intent)
        }

        // configura el listener para el botón "Mis Checks".
        // al pulsarlo, se inicia la `ActividadRegistro` para ver las revisiones anteriores.
        botonMisChecks.setOnClickListener {
            val intent = Intent(this, ActividadRegistro::class.java)
            // pasa el usuario y el almacén a la siguiente actividad.
            intent.putExtra(ActividadUsuarios.EXTRA_USUARIO, usuario)
            intent.putExtra(ActividadUsuarios.EXTRA_ALMACEN, almacen)
            startActivity(intent)
        }

        // configura el listener para el botón "ATRÁS".
        // al pulsarlo, finaliza esta actividad y regresa a la actividad anterior en la pila.
        botonAtras.setOnClickListener { finish() }
    }
}
