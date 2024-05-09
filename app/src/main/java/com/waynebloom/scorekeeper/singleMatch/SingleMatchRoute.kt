package com.waynebloom.scorekeeper.singleMatch

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme

@Composable
fun SingleMatchRoute(
    navController: NavHostController,
    viewModel: SingleMatchViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()

    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.game.color)
    val gameId = navController.currentBackStackEntry?.arguments?.getLong("gameId")!!

    val deletedToast = Toast.makeText(LocalContext.current, "Match deleted.", Toast.LENGTH_SHORT)
    val savedToast = Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

    UserSelectedPrimaryColorTheme(primaryColor) {
        SingleMatchScreen(
            uiState = uiState,
            onAddPlayerClick = {
                val route = "${Destination.EditPlayer.route}/$gameId/${viewModel.matchId}/-1"
                navController.navigate(route)
            },
            onDeleteClick = {
                viewModel.onDeleteClick()
                deletedToast.show()
                navController.popBackStack()
            },
            onPlayerClick = {
                val route = "${Destination.EditPlayer.route}/$gameId/${viewModel.matchId}/$it"
                navController.navigate(route)
            },
            onViewDetailedScoresClick = {
                navController.navigate(Destination.DetailPlayerScores.route)
            },
            onNotesChanged = viewModel::onNotesChanged,
            onSaveClick = {
                viewModel.onSaveClick()
                savedToast.show()
                navController.popBackStack()
            },
        )
    }
}
