package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun MatchCard(
	number: String,
	date: String,
	location: String,
	players: List<PlayerDomainModel>,
	totals: List<String>,
	modifier: Modifier = Modifier
) {
	val headline = if (location.isNotBlank()) {
		"$date ${stringResource(R.string.text_at)} $location"
	} else {
		date
	}

	Surface(
		shape = MaterialTheme.shapes.medium,
		tonalElevation = 2.dp,
		modifier = modifier.fillMaxWidth()
	) {
		Column(Modifier.padding(Spacing.screenEdge)) {
			Text(text = headline, style = MaterialTheme.typography.titleLarge)
			Text(
				text = "${stringResource(R.string.text_match)} #$number",
				style = MaterialTheme.typography.titleMedium
			)
			Spacer(Modifier.height(Spacing.sectionContent))
			if (players.isNotEmpty()) {
				Row(Modifier.height(IntrinsicSize.Max)) {
					Column(Modifier.weight(1f)) {
						for (i in players.indices step 2) {
							Row(
								horizontalArrangement = Arrangement.SpaceBetween,
								modifier = Modifier.fillMaxWidth()
							) {
								val textStyle = if (players[i].position == 0) {
									MaterialTheme.typography.bodyLarge.copy(
										color = MaterialTheme.colorScheme.primary,
										fontWeight = FontWeight.SemiBold
									)
								} else {
									MaterialTheme.typography.bodyLarge
								}
								Text(
									text = players[i].name,
									style = textStyle,
									overflow = TextOverflow.Ellipsis,
									maxLines = 1,
									modifier = Modifier
										.weight(1f, fill = false)
										.padding(end = 4.dp)
								)
								Text(text = totals[i], style = textStyle)
							}
						}
					}
					VerticalDivider(
						modifier = Modifier.padding(horizontal = Spacing.sectionContent)
					)
					Column(Modifier.weight(1f)) {
						for (i in 1 until players.size step 2) {
							Row(
								horizontalArrangement = Arrangement.SpaceBetween,
								modifier = Modifier.fillMaxWidth()
							) {
								val textStyle = if (players[i].position == 0) {
									MaterialTheme.typography.bodyLarge.copy(
										color = MaterialTheme.colorScheme.primary,
										fontWeight = FontWeight.SemiBold
									)
								} else {
									MaterialTheme.typography.bodyLarge
								}
								Text(
									text = players[i].name,
									style = textStyle,
									overflow = TextOverflow.Ellipsis,
									maxLines = 1,
									modifier = Modifier
										.weight(1f, fill = false)
										.padding(end = 4.dp)
								)
								Text(text = totals[i], style = textStyle)
							}
						}
					}
				}
			} else {
				Row {
					Icon(painter = painterResource(R.drawable.ic_help_circle), contentDescription = null)
					Spacer(Modifier.width(Spacing.sectionContent))
					Text(text = stringResource(R.string.text_empty_match_players))
				}
			}
		}
	}
}

@Preview
@Composable
private fun EmptyMatchCardPreview() {
	MedianMeepleTheme {
		MatchCard(
			number = "43",
			date = "4/17/24",
			location = "Conor's house",
			players = emptyList(),
			totals = (0..10 step 2).map { it.toString() }
		)
	}
}

@Preview
@Composable
private fun MatchCardPreview() {
	MedianMeepleTheme {
		MatchCard(
			number = "43",
			date = "4/17/24",
			location = "Conor's house",
			players = (0..5).map {
				PlayerDomainModel(
					name = "Player $it",
					position = it,
				)
			},
			totals = (0..10 step 2).map { it.toString() }
		)
	}
}
