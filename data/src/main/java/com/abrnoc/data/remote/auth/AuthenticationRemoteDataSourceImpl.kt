package com.abrnoc.data.remote.auth

import com.abrnoc.data.api.auth.AuthApi
import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import com.abrnoc.data.mappers.mapJwt
import com.abrnoc.data.remote.model.Jwt
import retrofit2.Response
import javax.inject.Inject

class AuthenticationRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
) : AuthenticationRemoteDataSource {
    override suspend fun sendCodeVerification(email: String): Response<Unit> {
        return authApi.setEmailForVerification(email)
    }

    override suspend fun signUpWithVerificationCode(
        password: String,
        email: String,
        code: String
    ): Jwt {
        return authApi.setAuthenticationSignUp(password, email, code).mapJwt()
    }


}