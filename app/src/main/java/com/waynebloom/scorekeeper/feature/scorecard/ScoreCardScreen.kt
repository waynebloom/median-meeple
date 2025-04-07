package com.waynebloom.scorekeeper.feature.scorecard

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.constants.Dimensions
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Size
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import com.waynebloom.scorekeeper.util.ext.onFocusSelectAll
import com.waynebloom.scorekeeper.util.ext.toRank
import com.waynebloom.scorekeeper.util.ext.toShortFormatString
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.util.SetDialogDestinationToEdgeToEdge
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun ScoreCardScreen(
	uiState: ScoreCardUiState,
	onPlayerClick: (Int) -> Unit,
	onSaveClick: () -> Unit,
	onDeleteClick: () -> Unit,
	onAddPlayer: (String, Int) -> Unit,
	onDeletePlayerClick: (Int) -> Unit,
	onCellChange: (TextFieldValue, col: Int, row: Int) -> Unit,
	onDialogTextFieldChange: (TextFieldValue) -> Unit,
	onDateChange: (Long) -> Unit,
	onLocationChange: (String) -> Unit,
	onNotesChange: (TextFieldValue) -> Unit,
	onPlayerChange: (String, Int) -> Unit
) {

	when (uiState) {
		is ScoreCardUiState.Loading -> {
			Loading()
		}

		is ScoreCardUiState.Content -> {
			ScoreCardScreen(
				gameName = uiState.game.name.text,
				matchNumber = uiState.indexOfMatch,
				categoryNames = uiState.categoryNames,
				hiddenCategories = uiState.hiddenCategories,
				players = uiState.players,
				scoreCard = uiState.scoreCard,
				totals = uiState.totals,
				dateMillis = uiState.dateMillis,
				location = uiState.location,
				notes = uiState.notes,
				playerIndexToChange = uiState.playerIndexToChange,
				manualRanks = uiState.manualRanks,
				dialogTextFieldValue = uiState.dialogTextFieldValue,
				onPlayerClick,
				onSaveClick,
				onDeleteClick,
				onAddPlayer,
				onDeletePlayerClick,
				onCellChange,
				onDialogTextFieldChange,
				onDateChange,
				onLocationChange,
				onNotesChange,
				onPlayerChange,
			)
		}
	}
}

@OptIn(
	ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
	ExperimentalFoundationApi::class
)
@Composable
private fun ScoreCardScreen(
	gameName: String,
	matchNumber: Int,
	categoryNames: List<String>,
	hiddenCategories: List<Int>,
	players: List<PlayerDomainModel>,
	scoreCard: List<List<ScoreDomainModel>>,
	totals: List<BigDecimal>,
	dateMillis: Long,
	location: String,
	notes: TextFieldValue,
	playerIndexToChange: Int,
	manualRanks: Boolean,
	dialogTextFieldValue: TextFieldValue,
	onPlayerClick: (Int) -> Unit,
	onSaveClick: () -> Unit,
	onDeleteClick: () -> Unit,
	onAddPlayer: (String, Int) -> Unit,
	onDeletePlayerClick: (Int) -> Unit,
	onCellChange: (TextFieldValue, col: Int, row: Int) -> Unit,
	onDialogTextFieldChange: (TextFieldValue) -> Unit,
	onDateChange: (Long) -> Unit,
	onLocationChange: (String) -> Unit,
	onNotesChange: (TextFieldValue) -> Unit,
	onPlayerChange: (String, Int) -> Unit,
) {

	var showDatePickerDialog by remember { mutableStateOf(false) }
	var showLocationDialog by remember { mutableStateOf(false) }
	var showMoreDialog by remember { mutableStateOf(false) }
	var showEditPlayerDialog by remember { mutableStateOf(false) }
	var showNewPlayerDialog by remember { mutableStateOf(false) }
	var showDeleteConfirmDialog by remember { mutableStateOf(false) }
	val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

	if (showDatePickerDialog) {
		DatePickerDialog(
			colors = DatePickerDefaults.colors(
				containerColor = MaterialTheme.colorScheme.surface
			),
			onDismissRequest = {
				showDatePickerDialog = false
			},
			confirmButton = {
				Button(
					onClick = {
						showDatePickerDialog = false
						onDateChange(datePickerState.selectedDateMillis ?: 0)
					},
					modifier = Modifier.padding(end = 12.dp, bottom = 12.dp)
				) {
					Text(stringResource(R.string.text_ok), style = MaterialTheme.typography.labelLarge)
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						showDatePickerDialog = false
					},
					modifier = Modifier.padding(bottom = 12.dp)
				) {
					Text(stringResource(R.string.text_cancel), style = MaterialTheme.typography.labelLarge)
				}
			}
		) {
			DatePicker(
				state = datePickerState,
				dateFormatter = DatePickerDefaults.dateFormatter()
			)
		}
	}
	if (showMoreDialog) {
		MoreDialog(
			onDismissRequest = {
				showMoreDialog = false
			},
			onDeleteClick = {
				showDeleteConfirmDialog = true
				showMoreDialog = false
			}
		)
	}
	if (showLocationDialog) {
		LocationDialog(
			value = dialogTextFieldValue,
			onValueChange = onDialogTextFieldChange,
			onDismissRequest = {
				showLocationDialog = false
			},
			onDone = {
				showLocationDialog = false
				onLocationChange(it)
			}
		)
	}
	if (showEditPlayerDialog) {
		Dialog(onDismissRequest = { showEditPlayerDialog = false }) {
			var nameValue by remember {
				mutableStateOf(TextFieldValue(players[playerIndexToChange].name))
			}
			var rank by remember {
				if (manualRanks) {
					players[playerIndexToChange].position.let {
						val validRanks = (1..players.size)
						if (validRanks.contains(it)) {
							mutableIntStateOf(it)
						} else {
							mutableIntStateOf(playerIndexToChange)
						}
					}
				} else {
					mutableIntStateOf(-1)
				}
			}
			val onDismiss = {
				showEditPlayerDialog = false
			}
			val onPositiveAction = {
				showEditPlayerDialog = false
				onPlayerChange(nameValue.text, rank)
			}
			Surface(
				shape = MaterialTheme.shapes.large,
				tonalElevation = 2.dp
			) {
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.padding(Dimensions.Spacing.dialogPadding)
				) {
					val focusRequester = remember { FocusRequester() }
					LaunchedEffect(true) {
						focusRequester.requestFocus()
					}

					Text(
						text = stringResource(R.string.text_edit_player),
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
					)
					OutlinedTextField(
						value = nameValue,
						onValueChange = { nameValue = it },
						modifier = Modifier
							.fillMaxWidth()
							.focusRequester(focusRequester)
							.onFocusSelectAll(nameValue) { nameValue = it },
						label = {
							Text(stringResource(R.string.field_name))
						},
						maxLines = 1,
						keyboardOptions = KeyboardOptions(
							capitalization = KeyboardCapitalization.Words,
							imeAction = ImeAction.Done
						),
						keyboardActions = KeyboardActions(
							onDone = { onPositiveAction() }
						)
					)
					if (manualRanks) {
						FlowRow {
							repeat(players.size) {
								val style = if (it == rank) {
									MaterialTheme.typography.bodyLarge.copy(
										color = MaterialTheme.colorScheme.primary,
										fontWeight = FontWeight.SemiBold
									)
								} else {
									MaterialTheme.typography.bodyLarge
								}
								val backgroundColor = if (it == rank) {
									MaterialTheme.colorScheme.secondaryContainer
								} else {
									Color.Transparent
								}
								Surface(
									color = backgroundColor,
									shape = CircleShape,
								) {
									Text(
										text = (it + 1).toRank(),
										style = style,
										modifier = Modifier
											.clickable { rank = it }
											.minimumInteractiveComponentSize()
											.padding(Dimensions.Spacing.sectionContent / 2)
									)
								}
							}
						}
					}
					Row(
						horizontalArrangement = Arrangement.SpaceBetween,
						modifier = Modifier.fillMaxWidth()
					) {
						TextButton(
							onClick = {
								showEditPlayerDialog = false
								onDeletePlayerClick(playerIndexToChange)
							}
						) {
							Text(
								text = stringResource(R.string.text_delete),
								style = MaterialTheme.typography.labelLarge,
								color = MaterialTheme.colorScheme.error
							)
						}
						Row {
							TextButton(onDismiss) {
								Text(
									text = stringResource(R.string.text_cancel),
									style = MaterialTheme.typography.labelLarge
								)
							}
							Spacer(Modifier.width(8.dp))
							Button(
								onClick = onPositiveAction,
								enabled = nameValue.text.isNotBlank(),

								) {
								Text(
									text = stringResource(R.string.text_ok),
									style = MaterialTheme.typography.labelLarge
								)
							}
						}
					}
				}
			}
		}
	}
	if (showNewPlayerDialog) {
		val focusRequester = remember { FocusRequester() }
		LaunchedEffect(true) {
			focusRequester.requestFocus()
		}
		var rank by remember { mutableIntStateOf(players.size) }
		val onPositiveAction = {
			showNewPlayerDialog = false
			onAddPlayer(dialogTextFieldValue.text, rank)
		}

		Dialog(onDismissRequest = { showEditPlayerDialog = false }) {
			Surface(
				shape = MaterialTheme.shapes.large,
				tonalElevation = 2.dp,
			) {
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.padding(Dimensions.Spacing.dialogPadding)
				) {
					Text(
						text = stringResource(R.string.text_new_player),
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
					)
					OutlinedTextField(
						value = dialogTextFieldValue,
						onValueChange = onDialogTextFieldChange,
						modifier = Modifier
							.fillMaxWidth()
							.focusRequester(focusRequester)
							.onFocusSelectAll(dialogTextFieldValue, onDialogTextFieldChange),
						label = {
							Text(stringResource(R.string.field_name))
						},
						maxLines = 1,
						keyboardOptions = KeyboardOptions(
							capitalization = KeyboardCapitalization.Words,
							imeAction = ImeAction.Done
						),
						keyboardActions = KeyboardActions(
							onDone = { onPositiveAction() }
						)
					)
					if (manualRanks) {
						FlowRow {
							repeat(players.size + 1) {
								val fontWeight = if (it == rank) {
									FontWeight.SemiBold
								} else {
									FontWeight.Normal
								}
								val backgroundColor = if (it == rank) {
									MaterialTheme.colorScheme.secondaryContainer
								} else {
									Color.Transparent
								}
								Surface(
									color = backgroundColor,
									shape = CircleShape,
								) {
									Text(
										text = (it + 1).toRank(),
										style = MaterialTheme.typography.bodyLarge,
										fontWeight = fontWeight,
										modifier = Modifier
											.clickable { rank = it }
											.minimumInteractiveComponentSize()
											.padding(Dimensions.Spacing.sectionContent / 2)
									)
								}
							}
						}
					}
					Row(
						horizontalArrangement = Arrangement.End,
						modifier = Modifier.fillMaxWidth()
					) {
						TextButton(onClick = { showNewPlayerDialog = false }) {
							Text(
								text = stringResource(R.string.text_cancel),
								style = MaterialTheme.typography.labelLarge
							)
						}
						Spacer(Modifier.width(8.dp))
						Button(
							onClick = onPositiveAction,
							enabled = dialogTextFieldValue.text.isNotBlank(),
						) {
							Text(
								text = stringResource(R.string.text_add),
								style = MaterialTheme.typography.labelLarge
							)
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

	Scaffold(
		topBar = {
			Surface {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.padding(start = Dimensions.Spacing.screenEdge, end = 4.dp)
						.defaultMinSize(minHeight = Size.topBarHeight)
						.windowInsetsPadding(WindowInsets.statusBars)
				) {
					Row(
						modifier = Modifier
							.weight(1f, fill = false)
							.padding(end = 12.dp)
					) {
						Text(
							text = gameName,
							style = MaterialTheme.typography.titleLarge,
							overflow = TextOverflow.Ellipsis,
							maxLines = 1,
							modifier = Modifier.weight(1f, fill = false)
						)
						Text(
							text = " #$matchNumber",
							style = MaterialTheme.typography.titleLarge,
						)
					}
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
		contentWindowInsets = WindowInsets(0.dp)
	) { innerPadding ->

		Column(
			Modifier
				.padding(innerPadding)
				.imePadding()
				.verticalScroll(rememberScrollState())
		) {
			Column(
				Modifier
					.fillMaxWidth()
					.padding(start = Dimensions.Spacing.screenEdge)
			) {
				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.subSectionContent),
				) {
					AssistChip(
						onClick = {
							showDatePickerDialog = true
						},
						label = {
							val formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
							formatter.timeZone = TimeZone.getTimeZone("UTC")
							Text(text = formatter.format(dateMillis))
						},
						leadingIcon = {
							Icon(
								painter = painterResource(R.drawable.ic_calendar),
								contentDescription = null,
								modifier = Modifier.size(InputChipDefaults.IconSize)
							)
						}
					)
					AssistChip(
						onClick = {
							onDialogTextFieldChange(TextFieldValue(location))
							showLocationDialog = true
						},
						label = {
							Text(text = location.ifBlank { stringResource(R.string.location_hint) })
						},
						modifier = Modifier.widthIn(max = 192.dp),
						leadingIcon = {
							Icon(
								imageVector = Icons.Outlined.Place,
								contentDescription = null,
								modifier = Modifier.size(InputChipDefaults.IconSize)
							)
						}
					)
				}

				var hasFocus by remember { mutableStateOf(false) }
				BasicTextField(
					value = notes,
					onValueChange = onNotesChange,
					modifier = Modifier
						.fillMaxWidth()
						.minimumInteractiveComponentSize()
						.padding(
							end = Dimensions.Spacing.screenEdge,
							bottom = Dimensions.Spacing.sectionContent
						)
						.onFocusChanged { hasFocus = it.hasFocus },
					textStyle = MaterialTheme.typography.bodyLarge.copy(
						color = MaterialTheme.colorScheme.onBackground
					),
					cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
					maxLines = 5,
					decorationBox = {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Icon(
								imageVector = Icons.Rounded.Edit,
								contentDescription = null,
								modifier = Modifier
									.padding(end = 8.dp)
									.size(20.dp),
								tint = MaterialTheme.colorScheme.primary
							)
							if (notes.text.isEmpty() && !hasFocus) {
								Text(text = stringResource(R.string.notes_hint))
							} else {
								it()
							}
						}
					}
				)
			}
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.padding(bottom = Dimensions.Spacing.sectionContent)
					.windowInsetsPadding(WindowInsets.navigationBars)
			) {
				val buttonModifier = if (players.isEmpty()) {
					Modifier.fillMaxWidth()
				} else {
					Modifier
				}
				if (players.isNotEmpty()) {
					LazyRow(
						contentPadding = PaddingValues(horizontal = Dimensions.Spacing.screenEdge),
						modifier = Modifier.padding(bottom = 8.dp)
					) {
						stickyHeader {
							Surface(
								color = MaterialTheme.colorScheme.secondaryContainer,
								shape = MaterialTheme.shapes.medium.copy(
									topEnd = CornerSize(0.dp),
									bottomEnd = CornerSize(0.dp)
								)
							) {
								Column(
									horizontalAlignment = Alignment.End,
									modifier = Modifier.width(intrinsicSize = IntrinsicSize.Max)
								) {

									// Blank space in the corner
									Box(
										contentAlignment = Alignment.CenterEnd,
										modifier = Modifier.size(60.dp)
									) {
										VerticalDivider(color = MaterialTheme.colorScheme.onSurface)
									}

									categoryNames
										.filterIndexed { i, _ -> !hiddenCategories.contains(i) }
										.forEach {
											Row(
												horizontalArrangement = Arrangement.End,
												modifier = Modifier
													.height(IntrinsicSize.Max)
													.fillMaxWidth()
											) {
												Box(
													contentAlignment = Alignment.CenterEnd,
													modifier = Modifier
														.padding(
															top = Dimensions.Spacing.sectionContent / 2,
															start = Dimensions.Spacing.screenEdge,
															bottom = Dimensions.Spacing.sectionContent / 2,
															end = Dimensions.Spacing.sectionContent / 2
														)
														.heightIn(min = 48.dp)
														.widthIn(min = 48.dp, max = 96.dp),
												) {
													Text(
														text = it,
														style = MaterialTheme.typography.bodyLarge,
														fontWeight = FontWeight.SemiBold,
														overflow = TextOverflow.Ellipsis,
														maxLines = 1,
													)
												}
												VerticalDivider(color = MaterialTheme.colorScheme.onSurface)
											}
										}

									if (categoryNames.size - hiddenCategories.size > 1) {
										Row(
											horizontalArrangement = Arrangement.End,
											modifier = Modifier
												.height(IntrinsicSize.Max)
												.fillMaxWidth()
										) {
											Box(
												contentAlignment = Alignment.CenterEnd,
												modifier = Modifier
													.padding(
														top = Dimensions.Spacing.sectionContent / 2,
														start = Dimensions.Spacing.screenEdge,
														bottom = Dimensions.Spacing.sectionContent / 2,
														end = Dimensions.Spacing.sectionContent / 2
													)
													.defaultMinSize(
														minHeight = 48.dp,
														minWidth = 48.dp
													),
											) {
												Text(
													text = stringResource(R.string.text_total),
													style = MaterialTheme.typography.bodyLarge,
													fontWeight = FontWeight.SemiBold,
													fontStyle = FontStyle.Italic
												)
											}
											VerticalDivider(color = MaterialTheme.colorScheme.onSurface)
										}
									}
								}
							}
						}

						itemsIndexed(players) { index, player ->
							val shape = if (index != players.lastIndex) {
								RectangleShape
							} else {
								MaterialTheme.shapes.medium.copy(
									topStart = CornerSize(0.dp),
									bottomStart = CornerSize(0.dp)
								)
							}
							Surface(
								shape = shape,
								tonalElevation = 2.dp
							) {
								ScoreColumn(
									playerName = player.name,
									playerRank = player.position,
									scores = scoreCard[index]
										.filterIndexed { i, _ -> !hiddenCategories.contains(i) },
									total = totals[index].toShortFormatString(),
									modifier = Modifier.animateItemPlacement(),
									onPlayerClick = {
										showEditPlayerDialog = true
										onPlayerClick(index)
									},
									onCellChange = { value, col -> onCellChange(value, col, index) },
								)
							}
						}
					}
				}
				Button(
					onClick = {
						onDialogTextFieldChange(TextFieldValue())
						showNewPlayerDialog = true
					},
					modifier = buttonModifier
						.align(Alignment.CenterHorizontally)
						.padding(horizontal = Dimensions.Spacing.screenEdge)
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
						Spacer(Modifier.width(8.dp))
						Text(text = stringResource(R.string.text_new_player))
					}
				}
			}
		}
	}
}

@Composable
private fun ScoreColumn(
	playerName: String,
	playerRank: Int,
	scores: List<ScoreDomainModel>,
	total: String,
	modifier: Modifier = Modifier,
	onPlayerClick: () -> Unit,
	onCellChange: (TextFieldValue, col: Int) -> Unit
) {
	val totalScoreHighlightColor = if (playerRank == 0) {
		MaterialTheme.colorScheme.secondaryContainer
	} else {
		Color.Transparent
	}
	Column(
		modifier
			.width(intrinsicSize = IntrinsicSize.Max)
			.widthIn(min = 48.dp, max = 144.dp)
	) {
		Box(
			contentAlignment = Alignment.CenterStart,
			modifier = modifier
				.fillMaxWidth()
				.clip(MaterialTheme.shapes.medium)
				.clickable(onClick = onPlayerClick)
				.heightIn(min = 60.dp),
		) {
			Column(
				modifier = Modifier.padding(
					horizontal = Dimensions.Spacing.sectionContent,
					vertical = Dimensions.Spacing.sectionContent / 2
				)
			) {
				Text(
					text = playerName,
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.SemiBold,
					overflow = TextOverflow.Ellipsis,
					maxLines = 1,
				)
				if (playerRank > -1) {
					Text(
						text = playerRank.plus(1).toRank(),
						style = MaterialTheme.typography.bodyMedium,
					)
				}
			}
		}

		scores.forEachIndexed { index, score ->
			val highlight = if (scores.size <= 1 && score.scoreAsTextFieldValue.text.isNotBlank()) {
				totalScoreHighlightColor
			} else {
				Color.Transparent
			}
			TextFieldCell(
				value = score.scoreAsTextFieldValue,
				onValueChange = { onCellChange(it, index) },
				modifier = Modifier.padding(Dimensions.Spacing.sectionContent / 2),
				unfocusedBackgroundColor = highlight,
				isError = score.scoreAsTextFieldValue.text.isNotBlank() && score.scoreAsBigDecimal == null,
			)
		}

		if (scores.size > 1) {
			Surface(
				shape = CircleShape,
				color = totalScoreHighlightColor,
				modifier = Modifier
					.padding(Dimensions.Spacing.sectionContent / 2)
					.defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
			) {
				Box(contentAlignment = Alignment.Center) {
					Text(
						text = total,
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.SemiBold,
						fontStyle = FontStyle.Italic,
						modifier = Modifier
							.padding(Dimensions.Spacing.sectionContent)
					)
				}
			}
		}
	}
}

@Composable
fun TextFieldCell(
	value: TextFieldValue,
	onValueChange: (TextFieldValue) -> Unit,
	modifier: Modifier = Modifier,
	unfocusedBackgroundColor: Color = Color.Transparent,
	isError: Boolean = false,
) {

	var hasFocus by remember { mutableStateOf(false) }
	val backgroundColor = when {
		hasFocus -> MaterialTheme.colorScheme.background
		isError -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
		else -> unfocusedBackgroundColor
	}
	val textStyle = if (isError) {
		MaterialTheme.typography.bodyLarge.copy(
			color = MaterialTheme.colorScheme.error,
			fontWeight = FontWeight.SemiBold,
			fontStyle = FontStyle.Italic
		)
	} else {
		MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
	}
	val backgroundColorAsState by animateColorAsState(targetValue = backgroundColor, label = "")
	val borderAlpha by animateFloatAsState(targetValue = if (hasFocus) 1f else 0f, label = "")

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.clip(MaterialTheme.shapes.small)
			.background(color = backgroundColorAsState)
			.border(
				width = 2.dp,
				color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha),
				shape = MaterialTheme.shapes.small
			)
			.heightIn(min = 48.dp)
	) {
		BasicTextField(
			value = value,
			onValueChange = onValueChange,
			modifier = Modifier
				.fillMaxWidth()
				.onFocusChanged { hasFocus = it.hasFocus }
				.padding(horizontal = Dimensions.Spacing.sectionContent),
			textStyle = textStyle,
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Next,
				keyboardType = KeyboardType.Decimal,
			),
			singleLine = true,
			cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
		)
	}
}

@Composable
fun MoreDialog(
	onDismissRequest: () -> Unit,
	onDeleteClick: () -> Unit,
) {
	Dialog(
		onDismissRequest = onDismissRequest,
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
					.clickable(onClick = onDismissRequest)
			)
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
						.padding(vertical = Dimensions.Spacing.screenEdge)
						.windowInsetsPadding(WindowInsets.navigationBars)
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.clickable(onClick = onDeleteClick)
							.padding(
								horizontal = Dimensions.Spacing.screenEdge,
								vertical = Dimensions.Spacing.sectionContent
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

@Composable
fun LocationDialog(
	value: TextFieldValue,
	onValueChange: (TextFieldValue) -> Unit,
	onDismissRequest: () -> Unit,
	onDone: (String) -> Unit
) {
	Dialog(onDismissRequest) {
		Surface(
			shape = MaterialTheme.shapes.large,
			tonalElevation = 2.dp
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.padding(Dimensions.Spacing.dialogPadding)
			) {
				val focusRequester = remember { FocusRequester() }
				LaunchedEffect(true) {
					focusRequester.requestFocus()
				}

				Text(
					text = stringResource(R.string.field_location),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
				)
				OutlinedTextField(
					value = value,
					onValueChange = onValueChange,
					modifier = Modifier
						.fillMaxWidth()
						.focusRequester(focusRequester)
						.onFocusSelectAll(value, onValueChange),
					maxLines = 1,
					keyboardOptions = KeyboardOptions(
						capitalization = KeyboardCapitalization.Words,
						imeAction = ImeAction.Done
					),
					keyboardActions = KeyboardActions(
						onDone = {
							onDone(value.text)
						}
					)
				)
				Row(
					horizontalArrangement = Arrangement.End,
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = Dimensions.Spacing.sectionContent)
				) {
					TextButton(onDismissRequest) {
						Text(
							text = stringResource(R.string.text_cancel),
							style = MaterialTheme.typography.labelLarge
						)
					}
					Spacer(Modifier.width(8.dp))
					Button({ onDone(value.text) }) {
						Text(
							text = stringResource(R.string.text_ok),
							style = MaterialTheme.typography.labelLarge
						)
					}
				}
			}
		}
	}
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Normal() {
	MedianMeepleTheme {
		ScoreCardScreen(
			uiState = ScoreCardSampleData.Default,
			onPlayerClick = {},
			onSaveClick = {},
			onDeleteClick = {},
			onAddPlayer = { _, _ -> },
			onDeletePlayerClick = {},
			onCellChange = { _, _, _ -> },
			onDialogTextFieldChange = {},
			onDateChange = {},
			onLocationChange = {},
			onNotesChange = {},
			onPlayerChange = { _, _ -> }
		)
	}
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LongData() {
	MedianMeepleTheme {
		ScoreCardScreen(
			uiState = ScoreCardSampleData.LongValues,
			onPlayerClick = {},
			onSaveClick = {},
			onDeleteClick = {},
			onAddPlayer = { _, _ -> },
			onDeletePlayerClick = {},
			onCellChange = { _, _, _ -> },
			onDialogTextFieldChange = {},
			onDateChange = {},
			onLocationChange = {},
			onNotesChange = {},
			onPlayerChange = { _, _ -> }
		)
	}
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoPlayers() {
	MedianMeepleTheme {
		ScoreCardScreen(
			uiState = ScoreCardSampleData.NoPlayers,
			onPlayerClick = {},
			onSaveClick = {},
			onDeleteClick = {},
			onAddPlayer = { _, _ -> },
			onDeletePlayerClick = {},
			onCellChange = { _, _, _ -> },
			onDialogTextFieldChange = {},
			onDateChange = {},
			onLocationChange = {},
			onNotesChange = {},
			onPlayerChange = { _, _ -> }
		)
	}
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OneCategory() {
	MedianMeepleTheme {
		ScoreCardScreen(
			uiState = ScoreCardSampleData.OneCategory,
			onPlayerClick = {},
			onSaveClick = {},
			onDeleteClick = {},
			onAddPlayer = { _, _ -> },
			onDeletePlayerClick = {},
			onCellChange = { _, _, _ -> },
			onDialogTextFieldChange = {},
			onDateChange = {},
			onLocationChange = {},
			onNotesChange = {},
			onPlayerChange = { _, _ -> }
		)
	}
}
