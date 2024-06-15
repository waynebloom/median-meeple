package com.waynebloom.scorekeeper.room.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.enums.ScoringMode

data class GameDomainModel(
    val id: Long = -1,
    val categories: List<CategoryDomainModel> = listOf(),
    val displayColorIndex: Int = 0,
    val matches: List<MatchDomainModel> = listOf(),
    val name: TextFieldValue = TextFieldValue(),
    val scoringMode: ScoringMode = ScoringMode.Descending
) {

    companion object {
        val DisplayColors = listOf(
            Color(0xFFE91E63),
            Color(0xFFEA1D43),
            Color(0xFFEC1C24),
            Color(0xFFE04226),
            Color(0xFFDF5A27),
            Color(0xFFD6731D),
            Color(0xFFF7A82E),
            Color(0xFFFFC107),
            Color(0xFFFFEB3B),
            Color(0xFFCDDC39),
            Color(0xFF8BC34A),
            Color(0xFF4CAF50),
            Color(0xFF009688),
            Color(0xFF00BCD4),
            Color(0xFF03A9F4),
            Color(0xFF2196F3),
            Color(0xFF3F51B5),
            Color(0xFF673AB7),
            Color(0xFF7C2697),
            Color(0xFF9C27B0),
            Color(0xFFC71EC4)
        )
    }
}
