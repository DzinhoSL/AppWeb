package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **Usuario**: Esta es una clase de datos (`data class`) que representa a un usuario
 * dentro del sistema de checklist. Contiene la información básica de un usuario,
 * incluyendo su identificación, nombre, rol y el almacén al que está asignado.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 *
 * @property id El identificador único del usuario. Es un número entero que lo distingue de otros usuarios.
 * @property nombre El nombre completo del usuario, por ejemplo, "Juan Hernández".
 * @property rol El rol del usuario dentro de la organización (ej. "Operario", "Supervisor"). Puede ser nulo.
 * @property idAlmacen El identificador del almacén al que está asignado este usuario.
 */
data class Usuario(
    val id: Int,
    val nombre: String,
    val rol: String?,
    val idAlmacen: Int
) : Serializable {
    
    /**
     * `obtenerIniciales`: Este método genera y devuelve las iniciales del usuario.
     * Intenta tomar la primera letra del primer nombre y la primera letra del segundo nombre
     * (si existen). Si solo hay un nombre, toma las dos primeras letras de ese nombre.
     * Esto es útil para mostrar una representación compacta del usuario en la interfaz de usuario.
     *
     * Ejemplos:
     * - "Juan Hernández" -> "J.H"
     * - "María" -> "MA"
     * - "" -> "?"
     *
     * @return Una cadena que contiene las iniciales del usuario, o "?" si el nombre está vacío.
     */
    fun obtenerIniciales(): String {
        // Divide el nombre completo en partes usando el espacio como delimitador y elimina espacios extra.
        val partes = nombre.trim().split(" ")
        return when {
            // Si hay al menos dos partes (nombre y apellido), toma la primera letra de cada una.
            partes.size >= 2 -> "${partes[0].first()}.${partes[1].first()}"
            // Si solo hay una parte (un solo nombre), toma las dos primeras letras y las convierte a mayúsculas.
            partes.size == 1 -> partes[0].take(2).uppercase()
            // Si el nombre está vacío o no tiene partes, devuelve un signo de interrogación.
            else -> "?"
        }
    }
}
