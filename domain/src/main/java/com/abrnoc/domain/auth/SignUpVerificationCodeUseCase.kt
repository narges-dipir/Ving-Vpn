package com.abrnoc.domain.auth

import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import com.abrnoc.data.local.AuthDataStore
import com.abrnoc.data.remote.auth.AuthenticationRemoteDataSource
import com.abrnoc.data.remote.model.Jwt
import com.abrnoc.domain.common.FlowUseCase
import com.abrnoc.domain.common.Result
import com.abrnoc.domain.model.VerificationObject
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