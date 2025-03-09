package com.waynebloom.scorekeeper.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class PreferencesManager @Inject constructor(
	@ApplicationContext context: Context
) {

	private val dataStore = context.dataStore

	suspend fun setEmail(email: String) {
		dataStore.edit { preferences ->
			preferences[Keys.EMAIL] = email
		}
	}

	suspend fun setLastSynced(lastSynced: String) {
		dataStore.edit { preferences ->
			preferences[Keys.LAST_SYNCED] = lastSynced
		}
	}

	suspend fun setUsername(username: String) {
		dataStore.edit { preferences ->
			preferences[Keys.USERNAME] = username
		}
	}

	suspend fun getLastSynced(): String? {
		return dataStore.data
			.map { preferences ->
				preferences[Keys.LAST_SYNCED]
			}
			.firstOrNull()
	}

	val email: Flow<String> = dataStore.data.map { preferences ->
		preferences[Keys.EMAIL] ?: ""
	}

	val username: Flow<String?> = dataStore.data.map { preferences ->
		preferences[Keys.USERNAME]
	}

	private object Keys {
		val EMAIL = stringPreferencesKey("email")
		val LAST_SYNCED = stringPreferencesKey("last_synced")
		val USERNAME = stringPreferencesKey("username")
	}
}