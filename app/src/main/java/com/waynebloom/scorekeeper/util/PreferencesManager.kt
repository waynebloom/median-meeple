package com.waynebloom.scorekeeper.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.waynebloom.scorekeeper.feature.settings.model.AppearanceMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class PreferencesManager @Inject constructor(
	@ApplicationContext context: Context
) {

	private val dataStore = context.dataStore

	val appearanceMode: Flow<String>
		get() = dataStore.data.map { prefs ->
			prefs[Keys.APPEARANCE] ?: AppearanceMode.SYSTEM.name
		}

	suspend fun setAppearanceMode(value: AppearanceMode) {
		dataStore.edit { pref ->
			pref[Keys.APPEARANCE] = value.name
		}
	}

	private object Keys {
		val APPEARANCE = stringPreferencesKey("appearance")
	}
}