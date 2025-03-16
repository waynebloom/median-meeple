package com.waynebloom.scorekeeper.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

sealed class TopLevelDestination(
	@StringRes val label: Int,
	@DrawableRes val icon: Int,
) {

	data object Hub : TopLevelDestination(R.string.text_at, R.drawable.ic_home)
	data object Library : TopLevelDestination(R.string.text_at, R.drawable.ic_grid)
	data object Settings : TopLevelDestination(R.string.text_at, R.drawable.ic_settings)
}