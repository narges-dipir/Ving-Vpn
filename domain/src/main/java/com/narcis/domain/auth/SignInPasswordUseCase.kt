package com.narcis.domain.auth

import com.narcis.data.di.utiles.DefaultDispatcherProvider
import com.narcis.data.local.AuthDataStore
import com.narcis.data.remote.auth.AuthenticationRemoteDataSource
import com.narcis.domain.common.FlowUseCase
import com.narcis.domain.common.Result
import com.narcis.domain.model.SignIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInPasswordUseCase @Inject constructor(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
    private val dataStore: AuthDataStore,
    dispatchers: DefaultDispatcherProvider

) : FlowUseCase<SignIn, String>(dispatchers.io) {
    override fun execute(parameters: SignIn): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)
            val result = authenticationRemoteDataSource.signInVerification(
                parameters.email,
                parameters.password
            )
            emit(Result.Success(result))
            if (result.isNotEmpty()) {
                dataStore.setVerifiedEmail(parameters.email)
                dataStore.setVerifiedPassword(parameters.password)
                dataStore.setJwtAuth(result)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

    }
}