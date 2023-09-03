package com.abrnoc.domain.di

import com.abrnoc.data.di.utiles.DefaultDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    fun provideDispatchers():DefaultDispatcherProvider = DefaultDispatcherProvider()
}