package com.narcis.data.api.auth

import com.narcis.data.api.auth.model.Email
import com.narcis.data.api.auth.model.JwtDto
import com.narcis.data.api.auth.model.SignIn
import com.narcis.data.api.auth.model.SignInResponse
import com.narcis.data.api.auth.model.SignUp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @GET("/gw/v1/user/check")
    suspend fun checkMail(
        @Query("email") email: String
    ): Response<Unit>

    @POST("/gw/v1/user/sendCode")
    suspend fun setEmailForVerification(
        @Body email: Email
    ): Response<Unit>
    @POST("/gw/v1/user/signup")
    suspend fun setAuthenticationSignUp(
       @Body signUp: SignUp
    ): JwtDto

    @POST("/gw/v1/user/login")
    suspend fun setAuthenticationSignIn(
       @Body signIn: SignIn
    ): SignInResponse


}