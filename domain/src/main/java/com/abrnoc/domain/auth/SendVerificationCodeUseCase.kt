package com.abrnoc.domain.auth

import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import com.abrnoc.data.remote.auth.AuthenticationRemoteDataSource
import com.abrnoc.domain.common.SuspendUseCase
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
    private val dispatchers: DefaultDispatcherProvider

) : SuspendUseCase<String, String>(dispatchers.io) {
    override suspend fun execute(parameters: String): String {
        return when (authenticationRemoteDataSource.sendCodeVerification(parameters).code()) {
            400 -> "User already exists for: $parameters"
            200 -> "The Code Was Send To Your Email"

            else -> "Connection Interrupt, Check your connection"
        }
    }
}