package com.abrnoc.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.abrnoc.data.local.PreferenceConstants.Auth_Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DateStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(context: Context): DataStore<androidx.datastore.preferences.core.Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(Auth_Prefs)
            }
        )
}