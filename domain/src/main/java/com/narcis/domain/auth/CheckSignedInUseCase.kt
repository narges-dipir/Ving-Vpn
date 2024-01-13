package com.narcis.domain.auth

import com.narcis.data.di.utiles.DefaultDispatcherProvider
import com.narcis.data.local.AuthDataStore
import com.narcis.domain.common.SuspendUseCase
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