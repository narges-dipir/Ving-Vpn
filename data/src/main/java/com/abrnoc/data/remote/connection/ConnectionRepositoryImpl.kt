package com.abrnoc.data.remote.connection

import com.abrnoc.data.api.connection.ConnectionApi
import com.abrnoc.data.api.connection.model.Url
import com.abrnoc.data.local.AuthDataStore
import com.abrnoc.data.remote.model.DefaultConfig
import javax.inject.Inject

class ConnectionRepositoryImpl @Inject constructor(
    private val connectionApi: ConnectionApi,
    private val dataStore: AuthDataStore,
) : ConnectionRepository {
    override suspend fun getAppConfigs(): List<DefaultConfig> {
        return connectionApi.getAllConfigs(dataStore.getJwtAuth()).mapToDefaultConfig()
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
