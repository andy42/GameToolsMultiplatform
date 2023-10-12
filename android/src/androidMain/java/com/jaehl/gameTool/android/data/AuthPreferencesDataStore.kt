package com.jaehl.gameTool.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import com.jaehl.gameTool.common.data.AuthLocalStore
import com.jaehl.gameTool.common.data.model.UserTokens
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class AuthPreferencesDataStore(
    private val context: Context
) : AuthLocalStore {

    override suspend fun getUserTokens(): UserTokens {
        return UserTokens(
            refreshToken = context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: "",
            accessToken = context.dataStore.data.first()[ACCESS_TOKEN_KEY] ?: "",
        )
    }

    override suspend fun saveToken(userTokens: UserTokens) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = userTokens.refreshToken
            preferences[ACCESS_TOKEN_KEY] = userTokens.accessToken
        }
    }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userTokens")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }
}