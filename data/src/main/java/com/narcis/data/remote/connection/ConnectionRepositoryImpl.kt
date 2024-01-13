package com.narcis.data.remote.connection

import com.narcis.data.api.connection.ConnectionApi
import com.narcis.data.api.connection.model.Url
import com.narcis.data.local.AuthDataStore
import com.narcis.data.remote.model.DefaultConfig
import javax.inject.Inject

class ConnectionRepositoryImpl @Inject constructor(
    private val connectionApi: ConnectionApi,
    private val dataStore: AuthDataStore,
) : ConnectionRepository {
    override suspend fun getAppConfigs(): List<DefaultConfig> {
        return connectionApi.getAllConfigs("Bearer "+dataStore.getJwtAuth()).mapToDefaultConfig()
    }

    private fun List<Url>.mapToDefaultConfig(): List<DefaultConfig> {
        return this.map { url ->
            DefaultConfig(
                address = url.address,
                alpn = url.alpn ?: "",
                country = url.country,
                fingerprint = url.fingerprint ?: "",
                flag = url.flag,
                username = url.username ?: "",
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
