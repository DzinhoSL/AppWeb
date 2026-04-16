package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **ItemChecklist**: Esta es una clase de datos que representa un único ítem
 * dentro de un checklist. Cada ítem tiene una descripción que detalla lo que debe ser revisado
 * y está asociado a una máquina específica. Es un componente fundamental para construir
 * los formularios de revisión.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 */
data class ItemChecklist(
    val id: Int,
    val idMaquina: Int,
    val descripcion: String
) : Serializable
