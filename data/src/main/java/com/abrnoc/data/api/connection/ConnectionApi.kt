package com.abrnoc.data.api.connection

import com.abrnoc.data.api.connection.model.Url
import retrofit2.http.GET
import retrofit2.http.Headers

interface ConnectionApi {
    @GET("/gw/v1/proxy")
    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MiIsImlhdCI6MTY5MTkyODcxOCwianRpIjoiMGQ5MHQ0NjU1ODFtZCIsImV4cCI6MTY5MzEzODMxOH0.SDkESm1km47pBdJDT4qERhgrwaQz5gxmoawVLRoYqLS_Fv-VJA0EhUAsr80i9bMBIkgOp9teJvHuOsxS0N2aPw")
    suspend fun getAllConfigs(): List<Url>
}
