package com.panelsandwich.checklist.utilidades

import android.content.Context
import android.content.SharedPreferences
import com.panelsandwich.checklist.modelos.Almacen
import com.panelsandwich.checklist.modelos.Usuario

/**
 * **SesionManager**: Este objeto singleton es una utilidad crucial para gestionar la sesión
 * del usuario dentro de la aplicación. Su propósito es almacenar de forma persistente
 * la información del usuario y el almacén seleccionados, de modo que estos datos
 * estén disponibles en cualquier parte de la aplicación y persistan incluso si la aplicación
 * se cierra y se vuelve a abrir. Utiliza `SharedPreferences` para el almacenamiento local.
 */
object SesionManager {

    // constantes para el nombre del archivo de SharedPreferences y las claves para guardar los datos.
    private const val PREFS_NOMBRE = "sesion_checklist"
    private const val CLAVE_USUARIO_ID = "usuario_id"
    private const val CLAVE_USUARIO_NOMBRE = "usuario_nombre"
    private const val CLAVE_USUARIO_ROL = "usuario_rol"
    private const val CLAVE_ALMACEN_ID = "almacen_id"
    private const val CLAVE_ALMACEN_NOMBRE = "almacen_nombre"

    /**
     * `obtenerPrefs`: Método privado de utilidad para obtener una instancia de `SharedPreferences`.
     * Centraliza la lógica para acceder al archivo de preferencias compartidas de la aplicación.
     *
     * @param context El contexto de la aplicación, necesario para acceder a `SharedPreferences`.
     * @return Una instancia de `SharedPreferences` para el archivo de sesión.
     */
    private fun obtenerPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
    }

    /**
     * `guardarSesion`: Almacena la información del usuario y el almacén seleccionados en `SharedPreferences`.
     * Esto permite que la aplicación recuerde la sesión del usuario entre diferentes usos.
     *
     * @param context El contexto de la aplicación.
     * @param usuario El objeto `Usuario` cuya información se va a guardar.
     * @param almacen El objeto `Almacen` cuya información se va a guardar.
     */
    fun guardarSesion(context: Context, usuario: Usuario, almacen: Almacen) {
        // Obtiene un editor para modificar las preferencias.
        val editor = obtenerPrefs(context).edit()
        // Guarda los datos del usuario.
        editor.putInt(CLAVE_USUARIO_ID, usuario.id)
        editor.putString(CLAVE_USUARIO_NOMBRE, usuario.nombre)
        editor.putString(CLAVE_USUARIO_ROL, usuario.rol)
        // Guarda los datos del almacén.
        editor.putInt(CLAVE_ALMACEN_ID, almacen.id)
        editor.putString(CLAVE_ALMACEN_NOMBRE, almacen.nombre)
        // Aplica los cambios de forma asíncrona.
        editor.apply()
    }

    /**
     * `obtenerUsuario`: Recupera la información del usuario guardada en `SharedPreferences`.
     * Si no hay datos de usuario guardados, devuelve `null`.
     *
     * @param context El contexto de la aplicación.
     * @return Un objeto `Usuario` si se encuentra información de sesión, o `null` en caso contrario.
     */
    fun obtenerUsuario(context: Context): Usuario? {
        val prefs = obtenerPrefs(context)
        // intenta obtener el ID del usuario. Si no existe, devuelve -1.
        val id = prefs.getInt(CLAVE_USUARIO_ID, -1)
        // si el ID es -1, significa que no hay usuario guardado.
        if (id == -1) return null
        // reconstruye el objeto Usuario a partir de los datos guardados.
        return Usuario(
            id = id,
            nombre = prefs.getString(CLAVE_USUARIO_NOMBRE, "") ?: "",
            rol = prefs.getString(CLAVE_USUARIO_ROL, null),
            idAlmacen = prefs.getInt(CLAVE_ALMACEN_ID, -1)
        )
    }

    /**
     * `obtenerAlmacen`: Recupera la información del almacén guardada en `SharedPreferences`.
     * Si no hay datos de almacén guardados, devuelve `null`.
     *
     * @param context El contexto de la aplicación.
     * @return Un objeto `Almacen` si se encuentra información de sesión, o `null` en caso contrario.
     */
    fun obtenerAlmacen(context: Context): Almacen? {
        val prefs = obtenerPrefs(context)
        // Intenta obtener el ID del almacén. Si no existe, devuelve -1.
        val id = prefs.getInt(CLAVE_ALMACEN_ID, -1)
        // Si el ID es -1, significa que no hay almacén guardado.
        if (id == -1) return null
        // Reconstruye el objeto Almacen a partir de los datos guardados.
        return Almacen(
            id = id,
            nombre = prefs.getString(CLAVE_ALMACEN_NOMBRE, "") ?: ""
        )
    }

    /**
     * `cerrarSesion`: Elimina toda la información de la sesión (usuario y almacén) de `SharedPreferences`.
     * Esto se utiliza, por ejemplo, cuando el usuario cierra sesión o cuando la sesión debe ser reiniciada.
     *
     * @param context El contexto de la aplicación.
     */
    fun cerrarSesion(context: Context) {
        // obtiene un editor y borra todas las entradas del archivo de preferencias, luego aplica los cambios.
        obtenerPrefs(context).edit().clear().apply()
    }
}
