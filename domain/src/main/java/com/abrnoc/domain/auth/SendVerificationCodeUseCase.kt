package com.abrnoc.domain.auth

import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import com.abrnoc.data.remote.auth.AuthenticationRemoteDataSource
import com.abrnoc.domain.common.SuspendUseCase
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
    dispatchers: DefaultDispatcherProvider

) : SuspendUseCase<String, Int>(dispatchers.io) {
    override suspend fun execute(parameters: String): Int {
        return try {
            val response = authenticationRemoteDataSource.sendCodeVerification(parameters)
            response.code()

        } catch (e: Exception) {
            0
        }
}
}