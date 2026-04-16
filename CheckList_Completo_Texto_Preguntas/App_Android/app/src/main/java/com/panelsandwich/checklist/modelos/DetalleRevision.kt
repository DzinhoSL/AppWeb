package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **DetalleRevision**: Esta es una clase de datos (`data class`) que representa un detalle
 * específico de una revisión de checklist. Cada instancia de `DetalleRevision` almacena la
 * respuesta de un ítem individual del checklist y, opcionalmente, un comentario asociado a ese ítem.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 *
 * @property id El identificador único del detalle de revisión. Es autogenerado por la base de datos, por eso tiene un valor por defecto de 0.
 * @property idRevision El identificador de la revisión a la que pertenece este detalle. Permite agrupar los ítems de un mismo checklist.
     * @property idItem El identificador del ítem del checklist al que corresponde este detalle.
     * @property resultado El resultado de la revisión para este ítem: `true` si el ítem está correcto (SÍ), `false` si hay un fallo (NO).
 */
data class DetalleRevision(
    val id: Int = 0,
    val idRevision: Int,
    val idItem: Int,
    val resultado: Boolean
) : Serializable
