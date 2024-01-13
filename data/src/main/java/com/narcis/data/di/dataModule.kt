package com.narcis.data.di

import com.narcis.data.remote.auth.AuthenticationRemoteDataSource
import com.narcis.data.remote.auth.AuthenticationRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class dataModule {
    @Binds
    abstract fun bindAuthDataStore(authenticationRemoteDataSourceImpl: AuthenticationRemoteDataSourceImpl): AuthenticationRemoteDataSource
}