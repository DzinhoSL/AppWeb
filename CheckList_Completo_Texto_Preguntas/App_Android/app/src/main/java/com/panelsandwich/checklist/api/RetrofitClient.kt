package com.panelsandwich.checklist.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // *** CAMBIA ESTA URL POR LA DE TU SERVIDOR ***
    // Ejemplos:
    //   Servidor en red local:  "http://192.168.1.100:8080/api/"
    //   Railway / Render:       "https://tu-app.railway.app/api/"
    //   Emulador → PC local:    "http://10.0.2.2:8080/api/"
    const val BASE_URL = "http://appseguridad.panelsandwich.group/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}
