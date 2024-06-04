package com.waynebloom.scorekeeper.singleMatch

import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.PreviewData

object SingleMatchSampleData {
    val Default = SingleMatchUiState(
        game = PreviewData.Games[0],
        match = PreviewData.Matches[0]
    )
}