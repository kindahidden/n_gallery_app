package com.elfen.ngallery.data.local.prefs

import android.content.Context
import androidx.compose.animation.fadeOut
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elfen.ngallery.data.local.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.Time
import java.time.Instant
import java.util.concurrent.TimeUnit

class StoreToken(private val context: Context) {
    companion object {
        val KEY = stringPreferencesKey("login_token")
        val AGENT_KEY = stringPreferencesKey("user_agent")
        val KEY_EXPIRATION = longPreferencesKey("login_expire")
        val EXPIRATION_DURATION = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
    }

    suspend fun setToken(token: String?) {

        context.dataStore.edit {
            if (token == null) {
                it.remove(KEY)
                it.remove(KEY_EXPIRATION)
            } else {
                it[KEY] = token
                it[KEY_EXPIRATION] = EXPIRATION_DURATION + Instant.now().toEpochMilli()
            }
        }
    }

    suspend fun setAgent(agent: String?) {
        context.dataStore.edit {
            if (agent == null) {
                it.remove(AGENT_KEY)
            } else {
                it[AGENT_KEY] = agent
            }
        }
    }

    val getAgent: Flow<String?> = context.dataStore.data.map { it[AGENT_KEY] }

    val getToken: Flow<String?> = context.dataStore.data.map { it[KEY] }
    val isTokenValid: Flow<Boolean> = context.dataStore.data.map {
        val expiration = it[KEY_EXPIRATION] ?: 0
        val now = Instant.now().toEpochMilli()

        expiration > now
    }
}