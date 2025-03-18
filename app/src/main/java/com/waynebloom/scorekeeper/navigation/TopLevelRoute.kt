package com.waynebloom.scorekeeper.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

enum class TopLevelDestination(
	@StringRes val label: Int,
	@DrawableRes val icon: Int,
	val route: String,
) {

	Hub(R.string.text_hub, R.drawable.ic_home, com.waynebloom.scorekeeper.navigation.Hub),
	Library(R.string.text_library, R.drawable.ic_grid, Destination.Library),
	Settings(R.string.text_settings, R.drawable.ic_settings, Destination.Settings)
}