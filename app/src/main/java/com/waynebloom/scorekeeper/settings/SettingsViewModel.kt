package com.waynebloom.scorekeeper.settings

import android.R.attr.mode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.auth.CredentialManager
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
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
	private val credentialManager: CredentialManager,
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
/* TODO: do something with this
		if (credentialManager.isEmpty()) {
			return
		}
*/

		viewModelScope.launch(Dispatchers.IO) {
			_uiState.update {
				val appearanceMode = AppearanceMode.valueOf(preferencesManager.appearanceMode.first())
				it.copy(
					appearanceMode = appearanceMode,
					email = preferencesManager.email.first(),
					name = preferencesManager.username.first(),
				)
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

	fun logout() {
		credentialManager.clear()
		_uiState.update {
			it.copy(isSignedIn = false, name = "", email = "")
		}
	}
}

private data class SettingsState(
	val isSignedIn: Boolean = false,
	val subDays: Int = 0,
	val name: String = "",
	val email: String = "",
	val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
) {

	fun toUiState(): SettingsUiState {
		if (!isSignedIn) {
			return SettingsUiState.SignedOut(appearanceMode)
		}

		return SettingsUiState.SignedIn(
			name = name,
			email = email,
			subDays = subDays,
			appearanceMode = appearanceMode
		)
	}
}

sealed interface SettingsUiState {
	val appearanceMode: AppearanceMode

	data class SignedOut(
		override val appearanceMode: AppearanceMode,
	) : SettingsUiState

	data class SignedIn(
		override val appearanceMode: AppearanceMode,
		val name: String,
		val email: String,
		val subDays: Int,
	) : SettingsUiState
}