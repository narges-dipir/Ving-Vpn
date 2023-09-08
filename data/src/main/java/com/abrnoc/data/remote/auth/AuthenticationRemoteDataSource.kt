package com.abrnoc.data.remote.auth

import com.abrnoc.data.remote.model.Jwt
import retrofit2.Response

interface AuthenticationRemoteDataSource {
    suspend fun sendCodeVerification(email: String): Response<Unit>
    suspend fun signUpWithVerificationCode( password: String, email: String, code: String) : Jwt
    suspend fun signInVerification(email: String, password: String) : String

}