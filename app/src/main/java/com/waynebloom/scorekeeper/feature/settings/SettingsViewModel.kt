package com.waynebloom.scorekeeper.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.feature.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	mutableStateFlowFactory: MutableStateFlowFactory,
	private val preferencesManager: PreferencesManager,
) : ViewModel() {

	private val _uiState = mutableStateFlowFactory.newInstance(SettingsState())
	val uiState = _uiState
		.onStart { loadData() }
		.map(SettingsState::toUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = _uiState.value.toUiState()
		)

	private fun loadData() {
		viewModelScope.launch(Dispatchers.IO) {
			_uiState.update {
				val appearanceMode = AppearanceMode.valueOf(preferencesManager.appearanceMode.first())
				it.copy(appearanceMode = appearanceMode)
			}
		}
	}

	fun onAppearanceModeSelect(mode: AppearanceMode) {
		_uiState.update {
			it.copy(appearanceMode = mode)
		}
		viewModelScope.launch(Dispatchers.IO) {
			preferencesManager.setAppearanceMode(mode)
		}
	}
}

private data class SettingsState(
	val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
) {

	fun toUiState(): SettingsUiState {
		return SettingsUiState(appearanceMode = appearanceMode)
	}
}

// NOTE: this screen will eventually hold more UI state than this.
data class SettingsUiState(
	val appearanceMode: AppearanceMode
)
