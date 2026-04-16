package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **Revision**: Esta es una clase de datos (`data class`) que representa una revisión completa
 * de una máquina realizada por un usuario. Contiene la información general de la revisión,
 * como quién la hizo, a qué máquina, cuándo, si se firmó y si se detectaron fallos.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 *
 * @property id El identificador único de la revisión. Es autogenerado por la base de datos, por eso tiene un valor por defecto de 0.
 * @property idUsuario El identificador del usuario que realizó la revisión.
 * @property idMaquina El identificador de la máquina que fue revisada.
 * @property fechaHora La fecha y hora en que se realizó la revisión, generalmente en formato de cadena.
 * @property firma La firma digital del usuario, almacenada como una cadena (por ejemplo, en formato Base64). Puede ser nula si la firma no es obligatoria o no se realizó.
 * @property tieneFallos Un booleano que indica si se detectaron fallos durante esta revisión (`true`) o si todo estaba correcto (`false`).
 */
data class Revision(
    val id: Int = 0,
    val idUsuario: Int,
    val idMaquina: Int,
    val fechaHora: String,
    val firma: String?,
    val tieneFallos: Boolean,
    val comentario: String? = null,
    val preguntasFallidas: List<String> = emptyList() // Lista de textos de preguntas fallidas
) : Serializable
