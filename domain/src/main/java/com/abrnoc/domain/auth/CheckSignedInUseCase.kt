package com.abrnoc.domain.auth

import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import com.abrnoc.data.local.AuthDataStore
import com.abrnoc.domain.common.SuspendUseCase
import javax.inject.Inject

class CheckSignedInUseCase @Inject constructor(
    private val dataStore: AuthDataStore,
    dispatchers: DefaultDispatcherProvider
): SuspendUseCase<Unit, Boolean>(dispatchers.io) {
    override suspend fun execute(parameters: Unit): Boolean {
        return (dataStore.getVerifiedEmail() != "") and (dataStore.getVerifiedPassword() != "") and
                (dataStore.getJwtAuth() != "")
    }


}