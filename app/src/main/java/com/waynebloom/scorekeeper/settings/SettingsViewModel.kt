package com.waynebloom.scorekeeper.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.auth.CredentialManager
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
): ViewModel() {

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
		if (credentialManager.isEmpty()) {
			return
		}

		viewModelScope.launch(Dispatchers.IO) {
			preferencesManager.email.combine(
				flow = preferencesManager.username
			) { email, username ->
				Pair(email, username)
			}.collectLatest { data ->
				_uiState.update {
					it.copy(
						isSignedIn = true,
						email = data.first,
						name = data.second ?: data.first
					)
				}
			}
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
) {

	fun toUiState(): SettingsUiState {
		if (!isSignedIn) {
			return SettingsUiState.SignedOut
		}

		return SettingsUiState.SignedIn(
			name = name,
			email = email,
			subDays = subDays,
		)
	}
}

sealed interface SettingsUiState {
	data object SignedOut: SettingsUiState
	data class SignedIn(
		val name: String,
		val email: String,
		val subDays: Int,
	): SettingsUiState
}