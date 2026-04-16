package com.panelsandwich.checklist.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    // Almacenes
    @GET("almacenes")
    suspend fun obtenerAlmacenes(): Response<List<JsonObject>>

    @POST("almacenes")
    suspend fun crearAlmacen(@Body almacen: JsonObject): Response<JsonObject>

    // Usuarios
    @GET("usuarios/almacen/{idAlmacen}")
    suspend fun obtenerUsuarios(@Path("idAlmacen") idAlmacen: Int): Response<List<JsonObject>>

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: JsonObject): Response<JsonObject>

    // Máquinas
    @GET("maquinas/almacen/{idAlmacen}")
    suspend fun obtenerMaquinas(@Path("idAlmacen") idAlmacen: Int): Response<List<JsonObject>>

    @POST("maquinas")
    suspend fun crearMaquina(@Body maquina: JsonObject): Response<JsonObject>

    // Checklist Items
    @GET("checklist_items/maquina/{idMaquina}")
    suspend fun obtenerChecklistItems(@Path("idMaquina") idMaquina: Int): Response<List<JsonObject>>

    @POST("checklist_items")
    suspend fun crearChecklistItem(@Body item: JsonObject): Response<JsonObject>

    // Revisiones
    @GET("revisiones")
    suspend fun obtenerRevisiones(): Response<List<JsonObject>>

    @GET("revisiones/maquina/{idMaquina}")
    suspend fun obtenerRevisionesPorMaquina(@Path("idMaquina") idMaquina: Int): Response<List<JsonObject>>

    @GET("revisiones/usuario/{idUsuario}")
    suspend fun obtenerRevisionesPorUsuario(@Path("idUsuario") idUsuario: Int): Response<List<JsonObject>>

    @POST("revisiones")
    suspend fun crearRevision(@Body revision: JsonObject): Response<JsonObject>

    // Detalles
    @GET("detalles/revision/{idRevision}")
    suspend fun obtenerDetalles(@Path("idRevision") idRevision: Int): Response<List<JsonObject>>

    @POST("detalles")
    suspend fun crearDetalle(@Body detalle: JsonObject): Response<JsonObject>

    // Health Check
    @GET("health")
    suspend fun healthCheck(): Response<JsonObject>
}
