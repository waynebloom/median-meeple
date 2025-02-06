package com.waynebloom.scorekeeper.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.constants.Dimensions

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
        onEmailChange,
        onPwChange,
        onLoginClick,
    )

    // TODO: Picking back up here. Need to transfer over the VM logic next
}

@Composable
private fun LoginScreen(
    email: TextFieldValue,
    pw: TextFieldValue,
    onEmailChange: (TextFieldValue) -> Unit,
    onPwChange: (TextFieldValue) -> Unit,
    onLoginClick: () -> Unit,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = pw,
            onValueChange = onPwChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Login")
        }
        Button(
            onClick = onRequestGames,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Request Games")
        }
    }
}
