package com.waynebloom.scorekeeper.singleMatch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.ext.toShortFormatString
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import java.math.BigDecimal

@Composable
fun NewSingleMatchScreen(
    uiState: NewSingleMatchUiState,
    onCellClick: (row: Int, col: Int) -> Unit,
    onCellEdit: (TextFieldValue, col: Int, row: Int) -> Unit,
) {

    // TODO remove old screen and rename this one

    NewSingleMatchScreen(
        categories = listOf(),
        players = listOf(),
        onCellClick,
        onCellEdit
    )
}

@Composable
private fun NewSingleMatchScreen(
    categories: List<CategoryDomainModel>,
    players: List<PlayerDomainModel>,
    onCellClick: (row: Int, col: Int) -> Unit,
    onCellEdit: (TextFieldValue, col: Int, row: Int) -> Unit
) {
    Scaffold { paddingValues ->
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent),
            contentPadding = PaddingValues(Dimensions.Spacing.screenEdge),
            modifier = Modifier.padding(paddingValues)
        ) {

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent),
                    modifier = Modifier.fillMaxWidth(0.3f)
                ) {

                    // Blank space in the corner
                    Box(modifier = Modifier.size(48.dp))

                    categories.forEachIndexed { index, category ->
                        UneditableCell(
                            text = category.name.text,
                            onClick = { onCellClick(0, index) }
                        )
                    }
                }
            }

            itemsIndexed(players) { index, player ->
                val row = index + 1
                ScoreColumn(
                    playerName = player.name.text,
                    categories = categories,
                    scores = player.categoryScores,
                    onCellClick = { column -> onCellClick(row, column) },
                    onCellEdit = { value, col -> onCellEdit(value, col, row) },
                )
            }
        }
    }
}

@Composable
private fun ScoreColumn(
    playerName: String,
    categories: List<CategoryDomainModel>,
    scores: List<CategoryScoreDomainModel>,
    onCellClick: (col: Int) -> Unit,
    onCellEdit: (TextFieldValue, col: Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent),
    ) {
        val scoresInOrder = scores.sortedBy {
            val parentCategory = categories.find { category ->
                category.id == it.categoryId
            }
            parentCategory?.position
        }

        UneditableCell(
            text = playerName,
            onClick = { onCellClick(0) }
        )

        scoresInOrder.forEachIndexed { index, categoryScore ->
            val column = index + 1
            EditableCell(
                value = TextFieldValue(categoryScore.score.toShortFormatString()),
                onValueChange = { onCellEdit(it, column) },
                onClick = { onCellClick(column) }
            )
        }
    }
}

@Composable
fun UneditableCell(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
    ) {

        Text(text = text)
    }
}

@Composable
fun EditableCell(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Preview
@Composable
private fun SingleMatchScreenPreview() {
    MedianMeepleTheme {
        NewSingleMatchScreen(
            categories = listOf(
                CategoryDomainModel(
                    id = 0,
                    name = TextFieldValue("Red"),
                    position = 0
                ),
                CategoryDomainModel(
                    id = 1,
                    name = TextFieldValue("Orange"),
                    position = 1
                ),
                CategoryDomainModel(
                    id = 2,
                    name = TextFieldValue("Yellow"),
                    position = 2
                ),
            ),
            players = listOf(
                PlayerDomainModel(
                    name = TextFieldValue("Wayne"),
                    categoryScores = listOf(
                        CategoryScoreDomainModel(
                            categoryId = 0,
                            score = BigDecimal.TEN
                        ),
                        CategoryScoreDomainModel(
                            categoryId = 1,
                            score = BigDecimal.ZERO
                        ),
                        CategoryScoreDomainModel(
                            categoryId = 2,
                            score = BigDecimal.ONE
                        ),
                    ),
                    position = 0
                ),
                PlayerDomainModel(
                    name = TextFieldValue("Conor"),
                    categoryScores = listOf(
                        CategoryScoreDomainModel(
                            categoryId = 0,
                            score = BigDecimal.ONE
                        ),
                        CategoryScoreDomainModel(
                            categoryId = 1,
                            score = BigDecimal.TEN
                        ),
                        CategoryScoreDomainModel(
                            categoryId = 2,
                            score = BigDecimal.ZERO
                        ),
                    ),
                    position = 0
                ),
                PlayerDomainModel(
                    name = TextFieldValue("Alyssa"),
                    categoryScores = listOf(
                        CategoryScoreDomainModel(
                            categoryId = 0,
                            score = BigDecimal.ZERO
                        ),
                        CategoryScoreDomainModel(
                            categoryId = 1,
                            score = BigDecimal.ONE
                        ),
                        CategoryScoreDomainModel(
                            categoryId = 2,
                            score = BigDecimal.TEN
                        ),
                    ),
                    position = 0
                ),
            ),
            onCellClick = {_,_->},
            onCellEdit = {_,_,_->}
        )
    }
}

@Preview
@Composable
private fun PlayerSectionBelowMaxPlayersPreview() {

}

@Preview
@Composable
private fun PlayerSectionAboveMaxPlayersPreview() {

}

@Preview
@Composable
private fun OtherSectionPreview() {

}
