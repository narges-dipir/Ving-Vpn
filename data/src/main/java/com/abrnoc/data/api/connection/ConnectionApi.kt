package com.abrnoc.data.api.connection

import com.abrnoc.data.api.connection.model.Url
import retrofit2.http.GET
import retrofit2.http.Headers

interface ConnectionApi {
    @GET("/gw/v1/proxy")
    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MiIsImlhdCI6MTY5MzgyNDY5NiwianRpIjoiMGRnMnRwcjN3aGE0MyIsImV4cCI6MTY5NTAzNDI5Nn0.MGIX7LXtrVUX4Y2uvCiQt0IZw5N0YTxWWL2OIFjD9eu1K6h7J2Q5P7UcgLuW-wARFOJhbGF0McwG0rK6OHBYPg")
    suspend fun getAllConfigs(
//        @Header("Authorization") jwt: String,
    ): List<Url>
}
