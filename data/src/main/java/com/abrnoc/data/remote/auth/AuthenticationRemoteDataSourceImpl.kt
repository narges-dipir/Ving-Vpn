package com.abrnoc.data.remote.auth

import com.abrnoc.data.api.auth.AuthApi
import com.abrnoc.data.api.auth.model.Email
import com.abrnoc.data.api.auth.model.SignIn
import com.abrnoc.data.api.auth.model.SignUp
import com.abrnoc.data.mappers.mapJwt
import com.abrnoc.data.remote.model.Jwt
import retrofit2.Response
import javax.inject.Inject

class AuthenticationRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,

) : AuthenticationRemoteDataSource {
    override suspend fun checkMail(email: String): Response<Unit> {
        return authApi.checkMail(email)
    }

    override suspend fun sendCodeVerification(email: String): Response<Unit> {
        return authApi.setEmailForVerification(Email(email))
    }

    override suspend fun signUpWithVerificationCode(
        password: String,
        email: String,
        code: String
    ): Jwt {
        return authApi.setAuthenticationSignUp(SignUp(password, email, code)).mapJwt()
    }

    override suspend fun signInVerification(email: String, password: String): String {
        return authApi.setAuthenticationSignIn(SignIn(email, password)).jwt
    }


}