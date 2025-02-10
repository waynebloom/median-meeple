package com.waynebloom.scorekeeper.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination

@Composable
fun LoginRoute(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPwChange = viewModel::onPwChange,
        onLoginClick = {
            viewModel.onLoginClick {
                navController.navigate(Destination.Library.route) {
                    popUpTo(Destination.Login.route) {
                        inclusive = true
                    }
                }
            }
        }
    )
}