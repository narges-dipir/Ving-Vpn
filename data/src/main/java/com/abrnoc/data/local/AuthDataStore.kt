package com.abrnoc.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class AuthDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {



    private suspend fun save(key: String, value: String) {
//        val dataStoreKey = preferencesKey<String>(key)
    }
}

