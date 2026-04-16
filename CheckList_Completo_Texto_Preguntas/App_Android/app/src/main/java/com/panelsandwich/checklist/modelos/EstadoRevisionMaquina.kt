package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **EstadoRevisionMaquina**: Esta es una clase de datos (`data class`) que se utiliza para
 * encapsular el estado de la revisión de una máquina específica, especialmente diseñada
 * para ser mostrada en la pantalla de Registro/Resumen (`ActividadRegistro`).
 * Permite agrupar la información de la máquina con el resultado de su última revisión,
 * incluyendo si se detectaron fallos y cualquier comentario asociado.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 *
 * @property maquina El objeto `Maquina` al que se refiere este estado de revisión.
 * @property tieneFallos Un booleano opcional (`Boolean?`) que indica el resultado general de la revisión:
 *                       - `true`: La máquina fue revisada y se encontraron fallos (se mostrará en rojo).
 *                       - `false`: La máquina fue revisada y no se encontraron fallos (se mostrará en verde).
 *                       - `null`: La máquina aún no ha sido revisada (se mostrará en un color neutro, como azul).
 * @property comentario El comentario general asociado a la revisión de la máquina, si lo hubiera.
 *                      Por defecto, es una cadena vacía.
 * @property revision El objeto `Revision` completo asociado a este estado, si existe. Puede ser `null` si no hay revisión.
 * @property detalles Una lista de objetos `DetalleRevision` que corresponden a los ítems individuales
 *                      del checklist de esta revisión. Por defecto, es una lista vacía.
 */
data class EstadoRevisionMaquina(
    val maquina: Maquina,
    var tieneFallos: Boolean? = null,
    var comentario: String = "",
    var revision: Revision? = null,
    var detalles: List<DetalleRevision> = emptyList()
) : Serializable
