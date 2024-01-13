package com.narcis.domain.connection

import com.narcis.data.di.utiles.DefaultDispatcherProvider
import com.narcis.data.remote.connection.ConnectionRepository
import com.narcis.domain.common.FlowUseCase
import com.narcis.domain.common.Result
import com.narcis.domain.common.mapToDomainConfig
import com.narcis.domain.model.DefaultConfigResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDefaultConfigUseCase @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    dispatchers: DefaultDispatcherProvider
) : FlowUseCase<Unit, List<DefaultConfigResult>>(dispatchers.io) {
    override fun execute(parameters: Unit): Flow<Result<List<DefaultConfigResult>>> = flow {
        try {
            emit(Result.Loading)
            val result = connectionRepository.getAppConfigs()
            emit(Result.Success(result.map { it.mapToDomainConfig() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

}