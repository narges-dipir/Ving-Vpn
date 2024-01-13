package com.narcis.domain.auth

import com.narcis.data.di.utiles.DefaultDispatcherProvider
import com.narcis.data.local.AuthDataStore
import com.narcis.data.remote.auth.AuthenticationRemoteDataSource
import com.narcis.data.remote.model.Jwt
import com.narcis.domain.common.FlowUseCase
import com.narcis.domain.common.Result
import com.narcis.domain.model.VerificationObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpVerificationCodeUseCase @Inject constructor(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
    private val dataStore: AuthDataStore,
    dispatchers: DefaultDispatcherProvider
) : FlowUseCase<VerificationObject, Jwt>(dispatchers.io) {
    override fun execute(parameters: VerificationObject): Flow<Result<Jwt>> = flow {
        try {
            emit(Result.Loading)
            val result = authenticationRemoteDataSource.signUpWithVerificationCode(
                parameters.password,
                parameters.email,
                parameters.code
            )
            emit(Result.Success(result))
            if (result.jwt.isNotEmpty()) {
                dataStore.setJwtAuth(jwt = result.jwt)
                dataStore.setVerifiedEmail(result.email)
                dataStore.setVerifiedPassword(parameters.password)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}