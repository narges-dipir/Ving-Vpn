package com.abrnoc.application.repository.remote

import retrofit2.http.GET
import retrofit2.http.Headers

interface DefaultConfigApi {
    @GET("/gw/v1/proxy")
    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MiIsImlhdCI6MTY5MzkwMTg0NSwianRpIjoiMGRnYzEwYmZtZm1tYyIsImV4cCI6MTY5NTExMTQ0NX0.5aGMunImg5bcLg2c066ht_DKflftJAVCBPyIRDQuNS2nqkl2tMnXSd4lE0Qg5VbC_tbj4W_jibaQ4R5Wn1nogw")
    suspend fun getAllConfigs(): List<Url>
}
