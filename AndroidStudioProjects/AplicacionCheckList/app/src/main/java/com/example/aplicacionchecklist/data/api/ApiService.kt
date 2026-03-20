package com.example.aplicacionchecklist.data.api

import com.example.aplicacionchecklist.data.modelo.Almacen
import com.example.aplicacionchecklist.data.modelo.Usuario
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("get_almacenes.php")
    suspend fun obtenerAlmacenes(): List<Almacen>

    @GET("get_usuarios.php")
    suspend fun obtenerUsuarios(
        @Query("almacen_id") almacenId: Int
    ): List<Usuario>
}