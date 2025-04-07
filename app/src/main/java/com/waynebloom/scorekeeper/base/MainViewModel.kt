package com.waynebloom.scorekeeper.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	preferencesManager: PreferencesManager,
): ViewModel() {

	val appearanceMode = preferencesManager.appearanceMode
		.map(AppearanceMode::valueOf)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Eagerly,
			initialValue = AppearanceMode.SYSTEM,
		)
}
