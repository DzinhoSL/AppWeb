package com.panelsandwich.checklist.modelos

import java.io.Serializable

/**
 * **Almacen**: Esta es una clase de datos (`data class`) que representa un almacén o nodo logístico
 * dentro del sistema. Es un modelo simple que encapsula la información básica de un almacén.
 * Implementa `Serializable` para poder ser pasada fácilmente entre diferentes actividades de Android
 * a través de un `Intent`.
 *
 * @property id El identificador único del almacén. Es un número entero que lo distingue de otros almacenes.
 * @property nombre El nombre descriptivo del almacén, por ejemplo, "Almacén Central" o "Almacén Norte".
 */
data class Almacen(
    val id: Int,
    val nombre: String
) : Serializable
