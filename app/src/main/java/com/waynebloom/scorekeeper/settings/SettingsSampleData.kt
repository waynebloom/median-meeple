package com.waynebloom.scorekeeper.settings

import com.waynebloom.scorekeeper.settings.model.AppearanceMode

internal object SettingsSampleData {
	val SignedIn = SettingsUiState.SignedIn(
		appearanceMode = AppearanceMode.SYSTEM,
		name = "Wayne Bloom",
		email = "wayne.bloom224@gmail.com",
		subDays = 17,
	)

	val SignedOut = SettingsUiState.SignedOut(
		appearanceMode = AppearanceMode.SYSTEM,
	)
}