package com.waynebloom.scorekeeper.editGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.components.RadioButtonOption
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.util.SetDialogDestinationToEdgeToEdge

@Composable
fun EditGameScreen(
	uiState: EditGameUiState,
	modifier: Modifier = Modifier,
	onSaveClick: () -> Unit,
	onCategoryClick: (Int) -> Unit,
	onCategoryDialogDismiss: () -> Unit,
	onCategoryInputChanged: (TextFieldValue, Int) -> Unit,
	onColorClick: (Int) -> Unit,
	onDeleteCategoryClick: (Int) -> Unit,
	onDeleteClick: () -> Unit,
	onDrag: (Offset) -> Unit,
	onDragEnd: () -> Unit,
	onDragStart: (Int) -> Unit,
	onEditButtonClick: () -> Unit,
	onHideCategoryInputField: () -> Unit,
	onNameChanged: (TextFieldValue) -> Unit,
	onNewCategoryClick: () -> Unit,
	onScoringModeChanged: (ScoringMode) -> Unit,
) {

	when (uiState) {
		is EditGameUiState.Loading -> Loading()
		is EditGameUiState.Content -> {

			MedianMeepleTheme {

				EditGameScreen(
					name = uiState.name,
					scoringMode = uiState.scoringMode,
					categories = uiState.categories,
					indexOfSelectedCategory = uiState.indexOfSelectedCategory,
					isCategoryDialogOpen = uiState.isCategoryDialogOpen,
					colorIndex = uiState.colorIndex,
					modifier = modifier,
					onSaveClick = onSaveClick,
					onCategoryClick = onCategoryClick,
					onCategoryDialogDismiss = onCategoryDialogDismiss,
					onCategoryInputChanged = onCategoryInputChanged,
					onColorClick = onColorClick,
					onDeleteCategoryClick = onDeleteCategoryClick,
					onDeleteClick = onDeleteClick,
					onDrag = onDrag,
					onDragEnd = onDragEnd,
					onDragStart = onDragStart,
					onEditButtonClick = onEditButtonClick,
					onHideCategoryInputField = onHideCategoryInputField,
					onNameChanged = onNameChanged,
					onNewCategoryClick = onNewCategoryClick,
					onScoringModeChanged = onScoringModeChanged
				)
			}
		}
	}
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditGameScreen(
	name: TextFieldValue,
	scoringMode: ScoringMode,
	categories: List<CategoryDomainModel>,
	indexOfSelectedCategory: Int,
	isCategoryDialogOpen: Boolean,
	colorIndex: Int,
	modifier: Modifier = Modifier,
	onSaveClick: () -> Unit,
	onCategoryClick: (Int) -> Unit,
	onCategoryDialogDismiss: () -> Unit,
	onCategoryInputChanged: (TextFieldValue, Int) -> Unit,
	onColorClick: (Int) -> Unit,
	onDeleteCategoryClick: (Int) -> Unit,
	onDeleteClick: () -> Unit,
	onDrag: (Offset) -> Unit,
	onDragEnd: () -> Unit,
	onDragStart: (Int) -> Unit,
	onEditButtonClick: () -> Unit,
	onHideCategoryInputField: () -> Unit,
	onNameChanged: (TextFieldValue) -> Unit,
	onNewCategoryClick: () -> Unit,
	onScoringModeChanged: (ScoringMode) -> Unit,
) {

	var showMoreDialog by remember { mutableStateOf(false) }
	var showDeleteConfirmDialog by remember { mutableStateOf(false) }

	if (showMoreDialog) {
		Dialog(
			onDismissRequest = {
				showMoreDialog = false
			},
			properties = DialogProperties(decorFitsSystemWindows = false)
		) {
			SetDialogDestinationToEdgeToEdge()

			Box(
				contentAlignment = Alignment.BottomStart,
				modifier = Modifier.fillMaxHeight()
			) {
				Box(
					Modifier
						.fillMaxSize()
						.clickable { showMoreDialog = false })
				Surface(
					tonalElevation = 2.dp,
					shape = MaterialTheme.shapes.medium.copy(
						bottomEnd = CornerSize(0.dp),
						bottomStart = CornerSize(0.dp)
					)
				) {
					Column(
						Modifier
							.fillMaxWidth()
							.padding(vertical = Spacing.screenEdge)
							.windowInsetsPadding(WindowInsets.navigationBars)
					) {
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.clickable {
									showDeleteConfirmDialog = true
									showMoreDialog = false
								}
								.padding(
									horizontal = Spacing.screenEdge,
									vertical = Spacing.sectionContent
								)
								.fillMaxWidth()
						) {
							Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
							Spacer(Modifier.width(8.dp))
							Text(text = stringResource(R.string.text_delete))
						}
					}
				}
			}
		}
	}
	if (showDeleteConfirmDialog) {
		AlertDialog(
			onDismissRequest = {
				showDeleteConfirmDialog = false
			},
			confirmButton = {
				Button(
					onClick = {
						showDeleteConfirmDialog = false
						onDeleteClick()
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error,
						contentColor = MaterialTheme.colorScheme.onError
					),
				) {
					Text(
						text = stringResource(R.string.text_delete),
						style = MaterialTheme.typography.labelLarge
					)
				}
			},
			dismissButton = {
				TextButton({ showDeleteConfirmDialog = false }) {
					Text(
						text = stringResource(R.string.text_cancel),
						style = MaterialTheme.typography.labelLarge
					)
				}
			},
			text = {
				Text(
					text = stringResource(R.string.text_delete_match_confirm),
					style = MaterialTheme.typography.bodyLarge,
				)
			},
			tonalElevation = 2.dp,
		)
	}

	if (isCategoryDialogOpen) {
		EditCategoriesBottomSheet(
			categories = categories,
			indexOfSelectedCategory = indexOfSelectedCategory,
			onCategoryClick = onCategoryClick,
			onDismiss = onCategoryDialogDismiss,
			onDeleteCategoryClick = onDeleteCategoryClick,
			onDrag = onDrag,
			onDragEnd = onDragEnd,
			onDragStart = onDragStart,
			onHideInputField = onHideCategoryInputField,
			onInputChanged = onCategoryInputChanged,
			onNewClick = onNewCategoryClick
		)
	}

	Scaffold(
		topBar = {
			Surface {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.windowInsetsPadding(WindowInsets.statusBars)
						.padding(start = Spacing.screenEdge, end = 4.dp)
						.defaultMinSize(minHeight = Size.topBarHeight)
						.fillMaxWidth()
				) {

					Text(
						text = name.text,
						style = MaterialTheme.typography.titleLarge,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1,
						modifier = Modifier.weight(1f, fill = false)
					)

					Row {
						Button(
							onClick = onSaveClick,
							colors = ButtonDefaults.buttonColors(
								containerColor = MaterialTheme.colorScheme.primary,
								contentColor = MaterialTheme.colorScheme.onPrimary
							),
						) {
							Text(text = stringResource(R.string.text_save))
						}
						Icon(
							imageVector = Icons.Rounded.MoreVert,
							contentDescription = null,
							modifier = Modifier
								.minimumInteractiveComponentSize()
								.clip(CircleShape)
								.clickable { showMoreDialog = true }
								.padding(4.dp)
						)
					}
				}
			}
		},
		contentWindowInsets = WindowInsets(0.dp),
	) {

		LazyColumn(
			contentPadding = PaddingValues(bottom = Spacing.screenEdge),
			modifier = modifier
				.padding(it)
				.imePadding()
		) {

			item {

				GameDetailsSection(
					selectedMode = scoringMode,
					nameTextFieldValue = name,
					isNameValid = name.text.isNotBlank(),
					onNameChanged = onNameChanged,
					onScoringModeClick = onScoringModeChanged,
					modifier = Modifier
						.padding(horizontal = Spacing.screenEdge)
						.padding(bottom = Spacing.betweenSections)
				)
			}

			item {

				Column(modifier = modifier.padding(bottom = Spacing.betweenSections)) {

					Text(
						text = stringResource(id = R.string.header_scoring_categories),
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						modifier = Modifier
							.padding(horizontal = Spacing.screenEdge)
							.padding(bottom = Spacing.subSectionContent),
					)
					if (categories.isNotEmpty()) {
						FlowRow(
							horizontalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
							modifier = modifier
								.fillMaxWidth()
								.padding(horizontal = Spacing.screenEdge)
						) {

							categories.forEachIndexed { index, category ->
								InputChip(
									selected = true,
									onClick = { onCategoryClick(index) },
									label = { Text(text = category.name.text) },
								)
							}
						}
					} else {
						HelperBox(
							message = stringResource(id = R.string.info_categories_section_helper),
							type = HelperBoxType.Info,
							modifier = Modifier.padding(horizontal = Spacing.screenEdge)
						)
					}
					Button(
						onClick = onEditButtonClick,
						modifier = Modifier
							.minimumInteractiveComponentSize()
							.padding(top = Spacing.sectionContent)
							.padding(horizontal = Spacing.screenEdge)
							.fillMaxWidth(),
					) {
						Text(text = stringResource(id = R.string.text_manage_categories))
					}
				}
			}

			item {

				CustomThemeSection(
					colorIndex = colorIndex,
					onColorClick = onColorClick,
					modifier = Modifier.padding(bottom = Spacing.betweenSections)
				)
			}

			item {
				Spacer(
					Modifier
						.windowInsetsBottomHeight(WindowInsets.navigationBars)
						.consumeWindowInsets(WindowInsets.navigationBars)
				)
			}
		}
	}
}

// region Game Details

@Composable
private fun GameDetailsSection(
	selectedMode: ScoringMode,
	nameTextFieldValue: TextFieldValue,
	isNameValid: Boolean,
	onNameChanged: (TextFieldValue) -> Unit,
	onScoringModeClick: (ScoringMode) -> Unit,
	modifier: Modifier = Modifier,
) {

	Column(
		verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
		modifier = modifier,
	) {

		OutlinedTextFieldWithErrorDescription(
			value = nameTextFieldValue,
			onValueChange = onNameChanged,
			label = { Text(text = stringResource(id = R.string.field_name)) },
			isError = !isNameValid,
			errorDescriptionResource = R.string.error_empty_name,
			selectAllOnFocus = true,
			shape = MaterialTheme.shapes.medium,
		)

		Column(modifier = Modifier.fillMaxWidth()) {

			ScoringMode.entries.forEach { option ->

				RadioButtonOption(
					menuOption = option,
					isSelected = selectedMode == option,
					onSelected = onScoringModeClick,
				)
			}
		}
	}
}

// endregion

// region Categories

@OptIn(
	ExperimentalFoundationApi::class,
	ExperimentalComposeUiApi::class,
	ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
)
@Composable
private fun EditCategoriesBottomSheetContent(
	categories: List<CategoryDomainModel>,
	indexOfSelectedCategory: Int,
	onCategoryClick: (Int) -> Unit,
	onInputChanged: (TextFieldValue, Int) -> Unit,
	onDeleteCategoryClick: (Int) -> Unit,
	onNewClick: () -> Unit,
	onDoneClick: () -> Unit,
	onCategoryDoneClick: () -> Unit,
	onDrag: (Offset) -> Unit,
	onDragStart: (Int) -> Unit,
	onDragEnd: () -> Unit,
) {
	var showDeleteConfirmState by remember { mutableStateOf(false) }

	Surface(
		tonalElevation = 2.dp,
		shape = MaterialTheme.shapes.medium.copy(
			bottomEnd = CornerSize(0.dp),
			bottomStart = CornerSize(0.dp),
		),
		modifier = Modifier.padding(top = 64.dp)
	) {
		Column(
			Modifier
				.padding(Spacing.dialogPadding)
				.imePadding()
				.windowInsetsPadding(WindowInsets.navigationBars)
		) {

			if (showDeleteConfirmState && indexOfSelectedCategory != -1) {
				Row {
					val dialogTitle = buildAnnotatedString {
						pushStyle(
							style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(
								fontWeight = FontWeight.SemiBold,
								color = MaterialTheme.colorScheme.error
							)
						)
						append("Delete ")
						withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
							append(categories[indexOfSelectedCategory].name.text)
						}
						append("?")
					}
					Icon(
						painter = painterResource(R.drawable.ic_error_circle),
						contentDescription = null,
						tint = MaterialTheme.colorScheme.error,
					)
					Spacer(Modifier.width(8.dp))
					Text(
						text = dialogTitle,
						modifier = Modifier.padding(bottom = Spacing.sectionContent)
					)
				}
				Text(
					text = "Any match with scores recorded in this category will be affected irreversibly.",
					modifier = Modifier.padding(bottom = Spacing.sectionContent)
				)
				Row(
					horizontalArrangement = Arrangement.End,
					modifier = Modifier.fillMaxWidth()
				) {
					val deletedToast =
						Toast.makeText(LocalContext.current, "Category deleted.", Toast.LENGTH_SHORT)

					TextButton(
						onClick = {
							onCategoryDoneClick()
							showDeleteConfirmState = false
						}
					) {
						Text(text = "Cancel")
					}
					Spacer(modifier = Modifier.width(Spacing.subSectionContent))
					Button(
						onClick = {
							onDeleteCategoryClick(indexOfSelectedCategory)
							deletedToast.show()
						},
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.error,
							contentColor = MaterialTheme.colorScheme.onError
						),
					) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Icon(
								imageVector = Icons.Rounded.Delete,
								contentDescription = null,
								modifier = Modifier
									.padding(end = 8.dp)
									.size(18.dp)
							)
							Text(text = "Delete Forever")
						}
					}
				}
			} else {
				Text(
					text = stringResource(R.string.text_manage_categories),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
					modifier = Modifier.padding(bottom = Spacing.sectionContent)
				)
				LazyColumn(
					verticalArrangement = Arrangement.spacedBy(Spacing.subSectionContent),
					modifier = Modifier
						.weight(1f, fill = false)
						.padding(bottom = Spacing.sectionContent)
				) {

					itemsIndexed(key = { index, _ -> index }, items = categories) { index, category ->
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.height(Size.minTappableSize)
						) {

							AnimatedContent(
								targetState = indexOfSelectedCategory,
								transitionSpec = { scaleIn() togetherWith scaleOut() },
								label = EditGameConstants.AnimationLabel.CategoryIcon,
							) {

								when (it) {

									index -> {  // confirm button

										Icon(
											painter = painterResource(id = R.drawable.ic_checkmark),
											contentDescription = null,
											modifier = Modifier
												.minimumInteractiveComponentSize()
												.clip(CircleShape)
												.clickable(onClick = onCategoryDoneClick)
												.padding(4.dp)
										)
									}

									-1 -> {   // drag handle
										Icon(
											painter = painterResource(id = R.drawable.ic_drag_handle),
											contentDescription = null,
											modifier = Modifier
												.size(48.dp)
												.padding(12.dp)
												.pointerInput(Unit) {
													detectDragGestures(
														onDragStart = { onDragStart(index) },
														onDragEnd = onDragEnd,
														onDrag = { _, dragAmount ->
															onDrag(
																dragAmount
															)
														}
													)
												},
											tint = MaterialTheme.colorScheme.onBackground,
										)
									}

									else -> {   // visual placeholder dot
										Box(
											Modifier
												.size(48.dp)
												.padding(20.dp)
												.background(
													color = MaterialTheme.colorScheme.onBackground,
													shape = CircleShape
												)
										)
									}
								}
							}

							if (index == indexOfSelectedCategory) {

								val focusRequester = remember { FocusRequester() }
								val bringIntoViewRequester = remember { BringIntoViewRequester() }

								LaunchedEffect(index) {
									bringIntoViewRequester.bringIntoView()
									focusRequester.requestFocus()
								}

								OutlinedTextFieldWithErrorDescription(
									value = category.name,
									onValueChange = { onInputChanged(it, index) },
									modifier = Modifier
										.weight(weight = 1f, fill = false)
										.padding(start = 4.dp)
										.focusRequester(focusRequester)
										.bringIntoViewRequester(bringIntoViewRequester),
									selectAllOnFocus = true,
									isError = category.name.text.isBlank(),
									errorDescriptionResource = R.string.field_error_empty,
									keyboardActions = KeyboardActions { onCategoryDoneClick() },
									keyboardOptions = KeyboardOptions.Default.copy(
										imeAction = ImeAction.Done
									),
									shape = MaterialTheme.shapes.medium,
									contentPadding = PaddingValues(Spacing.sectionContent),
									colors = OutlinedTextFieldDefaults.colors(
										focusedTextColor = MaterialTheme.colorScheme.onBackground,
										unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
										focusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(
											alpha = Alpha.MEDIUM_ALPHA
										),
									),
								)
								Icon(
									imageVector = Icons.Rounded.Delete,
									contentDescription = null,
									modifier = Modifier
										.minimumInteractiveComponentSize()
										.clip(CircleShape)
										.clickable {
											if (category.id != -1L) {
												showDeleteConfirmState = true
											} else {
												onDeleteCategoryClick(indexOfSelectedCategory)
											}
										}
										.padding(4.dp)
								)
							} else {

								Box(
									contentAlignment = Alignment.CenterStart,
									modifier = Modifier
										.weight(1f)
										.fillMaxHeight()
										.clip(shape = MaterialTheme.shapes.medium)
										.clickable {
											onCategoryClick(index)
										}
										.padding(start = 4.dp, end = Spacing.sectionContent)
								) {

									Text(
										text = category.name.text,
										color = MaterialTheme.colorScheme.onBackground,
										style = MaterialTheme.typography.bodyLarge
									)
								}
							}
						}
					}
				}
				Row(
					horizontalArrangement = Arrangement.End,
					modifier = Modifier.fillMaxWidth()
				) {
					Button(
						onClick = onNewClick,
						modifier = Modifier
					) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Icon(
								imageVector = Icons.Rounded.Add,
								contentDescription = null,
								modifier = Modifier
									.padding(end = 8.dp)
									.size(18.dp)
							)
							Text(text = "New")
						}
					}
					Spacer(modifier = Modifier.width(Spacing.sectionContent))
					Button(
						onClick = onDoneClick,
						modifier = Modifier
					) {
						Row(
							verticalAlignment = Alignment.CenterVertically,
						) {
							Icon(
								imageVector = Icons.Rounded.Done,
								contentDescription = null,
								modifier = Modifier
									.padding(end = 8.dp)
									.size(18.dp)
							)
							Text(text = "Done")
						}
					}
				}
			}
		}
	}
}

@Composable
private fun EditCategoriesBottomSheet(
	categories: List<CategoryDomainModel>,
	indexOfSelectedCategory: Int,
	onCategoryClick: (Int) -> Unit,
	onDismiss: () -> Unit,
	onDeleteCategoryClick: (Int) -> Unit,
	onDrag: (Offset) -> Unit,
	onDragEnd: () -> Unit,
	onDragStart: (Int) -> Unit,
	onHideInputField: () -> Unit,
	onInputChanged: (TextFieldValue, Int) -> Unit,
	onNewClick: () -> Unit,
) {

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(decorFitsSystemWindows = false),
	) {
		SetDialogDestinationToEdgeToEdge()

		Box(
			contentAlignment = Alignment.BottomStart,
			modifier = Modifier.fillMaxHeight()
		) {

			// showing this as a "bottom sheet" breaks the baseline "tap background to dismiss"
			// behavior. This code mimics it.
			Box(
				Modifier
					.clickable(onClick = onDismiss)
					.fillMaxSize()
			)

			EditCategoriesBottomSheetContent(
				categories,
				indexOfSelectedCategory,
				onCategoryClick,
				onInputChanged,
				onDeleteCategoryClick,
				onNewClick,
				onDismiss,
				onHideInputField,
				onDrag,
				onDragStart,
				onDragEnd,
			)
		}
	}
}

// endregion

@Composable
fun CustomThemeSection(
	colorIndex: Int,
	onColorClick: (Int) -> Unit,
	modifier: Modifier = Modifier
) {
	val lazyListState = rememberLazyListState(colorIndex)

	Column(
		verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
		modifier = modifier
	) {

		Text(
			text = stringResource(id = R.string.header_display_color),
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			modifier = Modifier.padding(horizontal = Spacing.screenEdge)
		)

		LazyRow(
			state = lazyListState,
			horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
			contentPadding = PaddingValues(horizontal = Spacing.screenEdge),
			modifier = modifier,
		) {

			itemsIndexed(GameDomainModel.DisplayColors) { i, color ->

				val tint = color.copy(alpha = 0.5f)
					.compositeOver(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))

				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier
						.size(64.dp)
						.clip(MaterialTheme.shapes.medium)
						.background(tint)
						.clickable { onColorClick(i) }
				) {

					androidx.compose.animation.AnimatedVisibility(
						visible = i == colorIndex,
						enter = fadeIn(),
						exit = fadeOut(),
					) {

						Icon(
							imageVector = Icons.Rounded.Check,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.background,
							modifier = Modifier.size(32.dp)
						)
					}
				}
			}
		}
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Normal() {
	MedianMeepleTheme {
		EditGameScreen(
			uiState = EditGameSampleData.Default,
			onSaveClick = {},
			onCategoryClick = {},
			onCategoryDialogDismiss = {},
			onCategoryInputChanged = { _, _ -> },
			onColorClick = {},
			onDeleteCategoryClick = {},
			onDeleteClick = {},
			onDrag = {},
			onDragEnd = {},
			onDragStart = {},
			onEditButtonClick = {},
			onHideCategoryInputField = {},
			onNameChanged = {},
			onNewCategoryClick = {},
			onScoringModeChanged = {},
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NoCategories() {
	MedianMeepleTheme {
		EditGameScreen(
			uiState = EditGameSampleData.NoCategories,
			onSaveClick = {},
			onCategoryClick = {},
			onCategoryDialogDismiss = {},
			onCategoryInputChanged = { _, _ -> },
			onColorClick = {},
			onDeleteCategoryClick = {},
			onDeleteClick = {},
			onDrag = {},
			onDragEnd = {},
			onDragStart = {},
			onEditButtonClick = {},
			onHideCategoryInputField = {},
			onNameChanged = {},
			onNewCategoryClick = {},
			onScoringModeChanged = {},
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EditingCategories() {
	val uiState = EditGameSampleData.CategoryDialog

	MedianMeepleTheme {
		EditCategoriesBottomSheet(
			categories = uiState.categories,
			indexOfSelectedCategory = uiState.indexOfSelectedCategory,
			onCategoryClick = {},
			onDismiss = {},
			onDeleteCategoryClick = {},
			onDrag = {},
			onDragEnd = {},
			onDragStart = {},
			onHideInputField = {},
			onInputChanged = { _, _ -> },
			onNewClick = {}
		)
	}
}
