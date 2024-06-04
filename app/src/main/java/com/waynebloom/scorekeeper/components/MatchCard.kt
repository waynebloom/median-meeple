package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme
import com.waynebloom.scorekeeper.theme.color.deepOrange500

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

    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(Spacing.screenEdge)) {
            Text(text = headline, style = MaterialTheme.typography.h6)
            Text(
                text = "${stringResource(R.string.text_match)} #$number",
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(Modifier.height(Spacing.sectionContent))
            Row(Modifier.height(IntrinsicSize.Max)) {
                Column(Modifier.weight(1f)) {
                    for (i in 1 until players.size step 2) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val textStyle = if (players[i].rank == 0) {
                                MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                MaterialTheme.typography.body1
                            }
                            Text(text = players[i].name, style = textStyle)
                            Text(text = totals[i], style = textStyle)
                        }
                    }
                }
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = Spacing.sectionContent)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    for (i in players.indices step 2) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val textStyle = if (players[i].rank == 0) {
                                MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                MaterialTheme.typography.body1
                            }
                            Text(text = players[i].name, style = textStyle)
                            Text(text = totals[i], style = textStyle)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MatchCardPreview() {
    UserSelectedPrimaryColorTheme(primaryColor = deepOrange500) {
        MatchCard(
            number = "43",
            date = "4/17/24",
            location = "Conor's house",
            players = (0..5).map {
                PlayerDomainModel(
                    name = "Player $it",
                    rank = it,
                )
            },
            totals = (0..10 step 2).map { it.toString() }
        )
    }
}
