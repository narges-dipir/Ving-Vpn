package com.abrnoc.data.api.auth

import com.abrnoc.data.api.auth.model.JwtDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("/gw/v1/user/sendCode")
    suspend fun setEmailForVerification(
        @Query("email") email: String
    ): Response<Unit>

    @POST("/gw/v1/user/signup")
    suspend fun setAuthenticationSignUp(
        @Query("password") password: String,
        @Query("email") email: String,
        @Query("code") code: String
    ): JwtDto


}