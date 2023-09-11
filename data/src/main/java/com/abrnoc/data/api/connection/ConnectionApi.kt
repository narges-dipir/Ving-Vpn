package com.abrnoc.data.api.connection

import com.abrnoc.data.api.connection.model.Url
import retrofit2.http.GET
import retrofit2.http.Header

interface ConnectionApi {
    @GET("/gw/v1/proxy")
    suspend fun getAllConfigs(
        @Header("Authorization") jwt: String,
    ): List<Url>
}
