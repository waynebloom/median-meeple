package com.waynebloom.scorekeeper.editPlayer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.IconButton
import com.waynebloom.scorekeeper.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.enums.ValidityState
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.isValidBigDecimal
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.theme.Animation

@Composable
fun EditPlayerScreen(
    uiState: EditPlayerUiState,
    onNameChange: (TextFieldValue) -> Unit,
    onRankChange: (TextFieldValue) -> Unit,
    onUseCategorizedScoreToggle: () -> Unit,
    onTotalScoreChange: (TextFieldValue) -> Unit,
    onCategoryScoreChange: (Int, TextFieldValue) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    EditPlayerScreen(
        game = uiState.game,
        name = uiState.name,
        isNameValid = uiState.isNameValid,
        rank = uiState.rank,
        isRankValid = uiState.isRankValid,
        useCategorizedScore = uiState.useCategorizedScore,
        categories = uiState.categories,
        categoryScores = uiState.categoryScores,
        categoryScoreValidityStates = uiState.categoryScoreValidityStates,
        totalScore = uiState.totalScore,
        totalScoreValidityState = uiState.totalScoreValidityState,
        onNameChange = onNameChange,
        onRankChange = onRankChange,
        onUseCategorizedScoreToggle = onUseCategorizedScoreToggle,
        onTotalScoreChange = onTotalScoreChange,
        onCategoryScoreChange = onCategoryScoreChange,
        onSaveClick = onSaveClick,
        onDeleteClick = onDeleteClick,
    )
}

@Composable
fun EditPlayerScreen(
    game: GameDomainModel,
    name: TextFieldValue,
    isNameValid: Boolean,
    rank: TextFieldValue,
    isRankValid: Boolean,
    useCategorizedScore: Boolean,
    categories: List<CategoryDomainModel>,
    categoryScores: List<TextFieldValue>,
    categoryScoreValidityStates: List<ValidityState>,
    totalScore: TextFieldValue,
    totalScoreValidityState: ValidityState,
    onNameChange: (TextFieldValue) -> Unit,
    onRankChange: (TextFieldValue) -> Unit,
    onUseCategorizedScoreToggle: () -> Unit,
    onTotalScoreChange: (TextFieldValue) -> Unit,
    onCategoryScoreChange: (Int, TextFieldValue) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    Scaffold(
        topBar = {
            val isAllDataValid = isNameValid
                .and(isRankValid)
                .and(categoryScoreValidityStates.all { it == ValidityState.Valid })
                .and(totalScoreValidityState == ValidityState.Valid)

            EditPlayerScoreScreenTopBar(
                title = name.text,
                submitButtonEnabled = isAllDataValid,
                onSaveClick = onSaveClick,
            )
        }
    ) { contentPadding ->

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = Dimensions.Spacing.screenEdge,
                vertical = Dimensions.Spacing.betweenSections
            ),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.betweenSections),
            modifier = Modifier.padding(contentPadding),
        ) {

            item {

                InformationSection(
                    name = name,
                    isError = !isNameValid,
                    onNameChanged = onNameChange,
                )
            }

            if (game.scoringMode == ScoringMode.Manual) {
                item {

                    RankingSection(
                        rank = rank,
                        isRankValid = !isRankValid,
                        onRankChange = onRankChange,
                    )
                }
            }

            item {

                ScoreSection(
                    categoryScores = categoryScores,
                    categories = categories,
                    totalScore = totalScore,
                    useCategorizedScore = useCategorizedScore,
                    onUseCategorizedScoreToggle = onUseCategorizedScoreToggle,
                    onCategoryScoreChange = { index, value -> onCategoryScoreChange(index, value) },
                    onTotalScoreChange = onTotalScoreChange,
                    onImeDone = onSaveClick,
                )
            }

            item {
                Divider()
                Spacer(Modifier.height(Dimensions.Spacing.sectionContent))

                // TODO: this needs a confirmation dialog before release
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .padding(top = Dimensions.Spacing.subSectionContent, bottom = Dimensions.Spacing.screenEdge)
                        .height(40.dp)
                        .fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.error,
                        backgroundColor = MaterialTheme.colors.background,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colors.error),
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
private fun EditPlayerScoreScreenTopBar(
    title: String,
    submitButtonEnabled: Boolean,
    onSaveClick: () -> Unit,
) {

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .defaultMinSize(minHeight = Dimensions.Size.topBarHeight)
                .fillMaxWidth(),
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            IconButton(
                imageVector = Icons.Rounded.Done,
                backgroundColor = Color.Transparent,
                enabled = submitButtonEnabled,
                onClick = onSaveClick
            )
        }

        Divider()
    }
}

@Composable
private fun InformationSection(
    name: TextFieldValue,
    isError: Boolean,
    onNameChanged: (TextFieldValue) -> Unit,
    focusManager: FocusManager = LocalFocusManager.current,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_player_info),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        OutlinedTextFieldWithErrorDescription(
            value = name,
            onValueChange = { onNameChanged(it) },
            label = { Text(text = stringResource(id = R.string.field_name)) },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescriptionResource = R.string.error_empty_name,
            selectAllOnFocus = true
        )
    }
}

@Composable
private fun RankingSection(
    rank: TextFieldValue,
    isRankValid: Boolean,
    onRankChange: (TextFieldValue) -> Unit,
    focusManager: FocusManager = LocalFocusManager.current,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_player_ranking),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        HelperBox(
            message = stringResource(R.string.helper_player_score_screen_rank),
            type = HelperBoxType.Info,
        )

        OutlinedTextFieldWithErrorDescription(
            value = rank,
            onValueChange = onRankChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text(text = stringResource(id = R.string.field_rank)) },
            isError = isRankValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescriptionResource = R.string.error_invalid_rank,
            selectAllOnFocus = true
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoreSection(
    categoryScores: List<TextFieldValue>,
    categories: List<CategoryDomainModel>,
    totalScore: TextFieldValue,
    useCategorizedScore: Boolean,
    onUseCategorizedScoreToggle: () -> Unit,
    onCategoryScoreChange: (Int, TextFieldValue) -> Unit,
    onTotalScoreChange: (TextFieldValue) -> Unit,
    onImeDone: () -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_player_scores),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(text = stringResource(id = R.string.field_detailed_view))

            Switch(
                checked = useCategorizedScore,
                onCheckedChange = { onUseCategorizedScoreToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    checkedTrackColor = MaterialTheme.colors.primary,
                ),
            )
        }

        AnimatedContent(
            targetState = useCategorizedScore,
            transitionSpec = { Animation.delayedFadeInWithFadeOut using Animation.sizeTransformWithDelay },
            label = "Scoring section expand/collapse"
        ) { useCategorizedScore ->

            if (useCategorizedScore) {

                if (categories.isNotEmpty()) {

                    CategoryFields(
                        categoryScores = categoryScores,
                        categories = categories,
                        onCategoryScoreChange = onCategoryScoreChange,
                        onImeDone = onImeDone,
                    )
                } else {

                    HelperBox(
                        message = stringResource(R.string.helper_player_score_screen_detailed_mode),
                        type = HelperBoxType.Error,
                    )
                }
            } else {

                val validityState = totalScore.text.isValidBigDecimal()

                OutlinedTextFieldWithErrorDescription(
                    value = totalScore,
                    onValueChange = onTotalScoreChange,
                    modifier = Modifier.padding(bottom = 8.dp),
                    label = { Text(text = stringResource(id = R.string.field_total_score)) },
                    isError = validityState != ValidityState.Valid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onImeDone() }
                    ),
                    errorDescriptionResource = validityState.descriptionResource,
                    selectAllOnFocus = true
                )
            }
        }
    }
}

@Composable
private fun CategoryFields(
    categoryScores: List<TextFieldValue>,
    categories: List<CategoryDomainModel>,
    onCategoryScoreChange: (Int, TextFieldValue) -> Unit,
    onImeDone: () -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)) {

        categoryScores.forEachIndexed { index, categoryScore ->

            val validityState = categoryScore.text.isValidBigDecimal()
            val focusManager = LocalFocusManager.current

            OutlinedTextFieldWithErrorDescription(
                value = categoryScores[index],
                onValueChange = { onCategoryScoreChange(index, it) },
                modifier = Modifier.fillMaxWidth(),
                selectAllOnFocus = true,
                label = { Text(text = categories[index].name.text) },
                isError = validityState != ValidityState.Valid,
                errorDescriptionResource = validityState.descriptionResource,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == categoryScores.lastIndex) {
                        ImeAction.Done
                    } else {
                        ImeAction.Next
                    }
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) },
                    onDone = { onImeDone() }
                ),
            )
        }
    }
}
