package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **Maquina**: Esta es una clase de datos (`data class`) que representa una máquina específica
 * dentro de un almacén. Es un modelo simple que encapsula la información básica de una máquina,
 * incluyendo su identificación, nombre y el almacén al que pertenece.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 *
 * @property id El identificador único de la máquina. Es un número entero que la distingue de otras máquinas.
 * @property nombre El nombre descriptivo de la máquina, por ejemplo, "Puente Grúa" o "Sierra de Corte".
 * @property idAlmacen El identificador del almacén al que está asignada esta máquina. Permite organizar las máquinas por ubicación.
 */
data class Maquina(
    val id: Int,
    val nombre: String,
    val idAlmacen: Int
) : Serializable
