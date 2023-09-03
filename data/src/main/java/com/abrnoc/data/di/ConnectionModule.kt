package com.abrnoc.data.di

import com.abrnoc.data.remote.connection.ConnectionRepository
import com.abrnoc.data.remote.connection.ConnectionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectionModule {
    @Binds
    abstract fun bindConfigRepository(
        iDefaultConfigRepositoryImpl: ConnectionRepositoryImpl,
    ): ConnectionRepository
}
