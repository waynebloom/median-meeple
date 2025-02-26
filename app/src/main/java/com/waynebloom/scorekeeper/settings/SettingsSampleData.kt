package com.waynebloom.scorekeeper.settings

internal object SettingsSampleData {
	val SignedIn = SettingsUiState.SignedIn(
		name = "Wayne Bloom",
		email = "wayne.bloom224@gmail.com",
		subDays = 17,
	)

	val SignedOut = SettingsUiState.SignedOut
}