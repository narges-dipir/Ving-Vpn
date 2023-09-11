package com.abrnoc.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.abrnoc.data.local.PreferenceConstants.Auth_Prefs
import com.abrnoc.data.local.PreferenceConstants.Email
import com.abrnoc.data.local.PreferenceConstants.ID
import com.abrnoc.data.local.PreferenceConstants.Jwt
import com.abrnoc.data.local.PreferenceConstants.Password
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        private val Context.dataStore by preferencesDataStore(Auth_Prefs)
        private val emailAuth = stringPreferencesKey(Email)
        private val passwordAuth = stringPreferencesKey(Password)
        private val jwtAuth = stringPreferencesKey(Jwt)
        private val id = intPreferencesKey(ID)
    }

    suspend fun setVerifiedEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[emailAuth] = email
        }
    }

    suspend fun getVerifiedEmail(): String =
        context.dataStore.data.firstOrNull()?.get(emailAuth) ?: ""

    suspend fun setVerifiedPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[passwordAuth] = password
        }
    }

    suspend fun getVerifiedPassword(): String =
        context.dataStore.data.firstOrNull()?.get(passwordAuth) ?: ""

    suspend fun setJwtAuth(jwt: String) {
        context.dataStore.edit { preferences ->
            preferences[jwtAuth] = jwt
        }
    }

    suspend fun getJwtAuth(): String =
        context.dataStore.data.firstOrNull()?.get(jwtAuth) ?: ""

    suspend fun setIdAuth(idAuth: Int) {
        context.dataStore.edit { preferences ->
            preferences[id] = idAuth
        }
    }
    suspend fun getIdAuth(): Int =
        context.dataStore.data.firstOrNull()?.get(id) ?: 0

}

