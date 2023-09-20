package com.abrnoc.domain.connection

import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import com.abrnoc.data.remote.connection.ConnectionRepository
import com.abrnoc.domain.common.FlowUseCase
import com.abrnoc.domain.common.Result
import com.abrnoc.domain.common.mapToDomainConfig
import com.abrnoc.domain.model.DefaultConfigResult
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