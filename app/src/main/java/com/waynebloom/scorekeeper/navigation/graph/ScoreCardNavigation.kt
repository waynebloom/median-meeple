package com.waynebloom.scorekeeper.navigation.graph

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.waynebloom.scorekeeper.scorecard.ScoreCardScreen
import com.waynebloom.scorekeeper.scorecard.ScoreCardViewModel
import kotlinx.serialization.Serializable

@Serializable
internal data class ScoreCard(
	val gameID: Long,
	val matchID: Long,
)

fun NavGraphBuilder.scoreCardDestination(onPopBackStack: () -> Unit) {
	composable<ScoreCard> {
		val viewModel: ScoreCardViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		val deletedToast = Toast.makeText(LocalContext.current, "Match deleted.", Toast.LENGTH_SHORT)
		val savedToast =
			Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

		// TODO: do I need this theme wrapper here?
		ScoreCardScreen(
			uiState = uiState,
			onPlayerClick = viewModel::onPlayerClick,
			onSaveClick = {
				viewModel.onSaveClick {
					onPopBackStack()
					savedToast.show()
				}
			},
			onDeleteClick = {
				viewModel.onDeleteClick {
					onPopBackStack()
					deletedToast.show()
				}
			},
			onAddPlayer = viewModel::onAddPlayer,
			onDeletePlayerClick = viewModel::onDeletePlayerClick,
			onCellChange = viewModel::onCellChange,
			onDialogTextFieldChange = viewModel::onDialogTextFieldChange,
			onDateChange = viewModel::onDateChange,
			onLocationChange = viewModel::onLocationChange,
			onNotesChange = viewModel::onNotesChange,
			onPlayerChange = viewModel::onPlayerChange,
		)
	}
}

fun NavController.navigateToScoreCard(gameID: Long, matchID: Long) {
	navigate(route = ScoreCard(gameID, matchID))
}
