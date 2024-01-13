package com.narcis.domain.auth

import com.narcis.data.di.utiles.DefaultDispatcherProvider
import com.narcis.data.remote.auth.AuthenticationRemoteDataSource
import com.narcis.domain.common.SuspendUseCase
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