package com.abrnoc.application.repository


import com.abrnoc.application.repository.model.DefaultConfig
import com.abrnoc.application.repository.model.ResultWrapper
import com.abrnoc.application.repository.remote.Url
import com.abrnoc.application.repository.remote.DefaultConfigApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class DefaultConfigRepositoryImpl @Inject constructor(
    private val defaultConfigApi: DefaultConfigApi,
) : IDefaultConfigRepository {
    override suspend fun getAppConfigs(): Flow<ResultWrapper<List<DefaultConfig>>> = flow {
        try {
            emit(ResultWrapper.Loading)
            val result = defaultConfigApi.getAllConfigs().mapToDefaultConfig()
            emit(ResultWrapper.Success(result))
            // ResultWrapper.Success()
        } catch (e: HttpException) {
            emit(ResultWrapper.Error(e))
        }
    }

    private fun List<Url>.mapToDefaultConfig(): List<DefaultConfig> {
        return this.map { url ->
            DefaultConfig(
                address = url.address,
                alpn = url.alpn ?: "",
                country = url.country,
                fingerprint = url.fingerprint ?: "",
                flag = url.flag,
                password = url.password,
                port = url.port,
                protocol = url.protocol,
                security = url.security ?: "",
                sni = url.sni ?: "",
                type = url.type,
                url = url.url,

            )
        }
    }
}
