package com.panelsandwich.checklist.utilidades

import java.text.SimpleDateFormat
import java.util.*

/**
 * **Utilidades**: Este objeto singleton contiene una colección de funciones de ayuda
 * generales que son útiles en varias partes de la aplicación. Su propósito es centralizar
 * la lógica común y repetitiva, como el formateo de fechas y horas, para evitar la duplicación
 * de código y mejorar la mantenibilidad.
 */
object Utilidades {

    /**
     * `obtenerFechaHoraActual`: Esta función devuelve la fecha y hora actual del sistema
     * formateada en un estilo legible para el usuario: "dd/MM/yy - HH:mm".
     * Es útil para mostrar la hora de eventos o registros en la interfaz de usuario.
     *
     * Ejemplo de salida: "19/03/26 - 8:03"
     *
     * @return Una cadena de texto con la fecha y hora actual formateada.
     */
    fun obtenerFechaHoraActual(): String {
        // define el formato deseado para la fecha y hora, utilizando el locale español para asegurar el formato correcto.
        val formato = SimpleDateFormat("dd/MM/yy - H:mm", Locale("es", "ES"))
        // formatea la fecha y hora actual y la devuelve como una cadena.
        return formato.format(Date())
    }

    /**
     * `obtenerFechaHoraMysql`: Esta función devuelve la fecha y hora actual del sistema
     * formateada en un estilo compatible con bases de datos MySQL: "yyyy-MM-dd HH:mm:ss".
     * Es esencial para almacenar registros de fecha y hora de manera uniforme en la base de datos.
     *
     * Ejemplo de salida: "2026-03-19 08:03:45"
     *
     * @return Una cadena de texto con la fecha y hora actual formateada para MySQL.
     */
    fun obtenerFechaHoraMysql(): String {
        // define el formato deseado para la fecha y hora, utilizando el locale por defecto del sistema.
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        // formatea la fecha y hora actual y la devuelve como una cadena.
        return formato.format(Date())
    }
}
