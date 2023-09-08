package com.abrnoc.application.repository

import com.abrnoc.application.repository.model.DefaultConfig
import com.abrnoc.application.repository.model.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface IDefaultConfigRepository {
    suspend fun getAppConfigs(): Flow<ResultWrapper<List<DefaultConfig>>>
}