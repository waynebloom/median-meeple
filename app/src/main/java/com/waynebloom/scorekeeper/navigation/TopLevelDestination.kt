package com.waynebloom.scorekeeper.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.navigation.graph.Hub
import com.waynebloom.scorekeeper.navigation.graph.LibrarySection
import com.waynebloom.scorekeeper.navigation.graph.SettingsSection
import kotlin.reflect.KClass

enum class TopLevelDestination(
	@StringRes val label: Int,
	@DrawableRes val icon: Int,
	val route: KClass<*>,
) {
	HUB(R.string.text_hub, R.drawable.ic_home, Hub::class),
	LIBRARY(R.string.text_library, R.drawable.ic_grid, LibrarySection::class),
	SETTINGS(R.string.text_settings, R.drawable.ic_settings, SettingsSection::class),
}
