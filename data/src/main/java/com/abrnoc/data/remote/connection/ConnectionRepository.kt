package com.abrnoc.data.remote.connection

import com.abrnoc.data.remote.model.DefaultConfig

interface ConnectionRepository {
    suspend fun getAppConfigs(): List<DefaultConfig>
}