package com.waynebloom.scorekeeper.feature.settings.model

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.common.MenuOption

enum class AppearanceMode(
	@StringRes override val label: Int
): MenuOption {
	SYSTEM(R.string.text_system),
	LIGHT(R.string.text_light),
	DARK(R.string.text_dark);
}