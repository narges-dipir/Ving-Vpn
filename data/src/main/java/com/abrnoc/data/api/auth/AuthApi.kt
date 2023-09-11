package com.abrnoc.data.api.auth

import com.abrnoc.data.api.auth.model.Email
import com.abrnoc.data.api.auth.model.JwtDto
import com.abrnoc.data.api.auth.model.SignIn
import com.abrnoc.data.api.auth.model.SignInResponse
import com.abrnoc.data.api.auth.model.SignUp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

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