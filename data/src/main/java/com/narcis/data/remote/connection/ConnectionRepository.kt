package com.narcis.data.remote.connection

import com.narcis.data.remote.model.DefaultConfig

interface ConnectionRepository {
    suspend fun getAppConfigs(): List<DefaultConfig>
}