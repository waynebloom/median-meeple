package com.waynebloom.scorekeeper.settings.login

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//	private val login: Login,
	mutableStateFlowFactory: MutableStateFlowFactory,
) : ViewModel() {

	// FIXME: reimplement auth w/ Supabase

	private val viewModelState: MutableStateFlow<LoginViewModelState>
	val uiState: StateFlow<LoginUiState>

	init {
		viewModelState = mutableStateFlowFactory.newInstance(LoginViewModelState())
		uiState = viewModelState
			.map(LoginViewModelState::toUiState)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.Eagerly,
				initialValue = viewModelState.value.toUiState()
			)
	}

	fun onEmailChange(value: TextFieldValue) = viewModelState.update {
		it.copy(email = value)
	}

	fun onPwChange(value: TextFieldValue) = viewModelState.update {
		it.copy(pw = value)
	}

	fun onLoginClick(onSuccess: () -> Unit) = viewModelScope.launch {
		viewModelState.update {
			it.copy(loading = true)
		}

		val email = viewModelState.value.email.text
		val pw = viewModelState.value.pw.text

		// FIXME: stubbed this off for now until reimplementation
		onSuccess()
		/*
				login(email, pw).collect { isSuccess ->
					if (isSuccess) {
						onSuccess()
					}

					viewModelState.update {
					TODO:
						show an error
						clear password

						it.copy(loading = false)
					}
				}
		*/
	}
}

private data class LoginViewModelState(
	val email: TextFieldValue = TextFieldValue(),
	val pw: TextFieldValue = TextFieldValue(),
	val loading: Boolean = false,
) {
	fun toUiState() = LoginUiState(email, pw, loading)
}

data class LoginUiState(
	val email: TextFieldValue,
	val pw: TextFieldValue,
	val loading: Boolean,
)
