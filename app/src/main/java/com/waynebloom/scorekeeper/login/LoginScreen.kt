package com.waynebloom.scorekeeper.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun LoginScreen(
	uiState: LoginUiState,
	onEmailChange: (TextFieldValue) -> Unit,
	onPwChange: (TextFieldValue) -> Unit,
	onLoginClick: () -> Unit,
) {

	LoginScreen(
		uiState.email,
		uiState.pw,
		uiState.loading,
		onEmailChange,
		onPwChange,
		onLoginClick,
	)
}

@Composable
private fun LoginScreen(
	email: TextFieldValue,
	pw: TextFieldValue,
	loading: Boolean,
	onEmailChange: (TextFieldValue) -> Unit,
	onPwChange: (TextFieldValue) -> Unit,
	onLoginClick: () -> Unit,
) {

	Scaffold(
		topBar = {

		},
	) { innerPadding ->
		Column(
			verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent),
			modifier = Modifier
				.padding(innerPadding)
				.padding(16.dp)
		) {
			OutlinedTextField(
				value = email,
				onValueChange = onEmailChange,
				label = {
					Text("Email")
				},
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				value = pw,
				onValueChange = onPwChange,
				label = {
					Text("Password")
				},
				modifier = Modifier.fillMaxWidth(),
				visualTransformation = PasswordVisualTransformation()
			)
			Button(
				onClick = onLoginClick,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text("Login")
			}
		}
	}
}

@Preview
@Composable
private fun LoginPreview() {
	MedianMeepleTheme {
		Box(Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
		) {
			LoginScreen(
				email = TextFieldValue("admin@test.com"),
				pw = TextFieldValue("password123"),
				loading = false,
				{}, {}, {}
			)
		}
	}
}
