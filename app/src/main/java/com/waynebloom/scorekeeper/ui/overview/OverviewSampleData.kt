package com.waynebloom.scorekeeper.ui.overview

import com.waynebloom.scorekeeper.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.viewmodel.OverviewUiState

object OverviewSampleData {
    val UiState = OverviewUiState(
        games = GameObjectsDefaultPreview,
        loading = false,
        matches = MatchObjectsDefaultPreview,
    )
}