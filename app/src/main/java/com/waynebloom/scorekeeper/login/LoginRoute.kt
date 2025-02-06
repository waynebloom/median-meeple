package com.waynebloom.scorekeeper.login

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun LoginRoute(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    // TODO: add uiState

    // TODO: add events
    LoginScreen()
}