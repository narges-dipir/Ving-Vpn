package com.abrnoc.application.di

import com.abrnoc.application.repository.DefaultConfigRepositoryImpl
import com.abrnoc.application.repository.IDefaultConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindConfigRepository(
        iDefaultConfigRepositoryImpl: DefaultConfigRepositoryImpl,
    ): IDefaultConfigRepository
}
