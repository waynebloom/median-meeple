package com.waynebloom.scorekeeper.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun GameCard(
	name: String,
	color: Color,
	highScore: String,
	noOfMatches: String,
	modifier: Modifier = Modifier
) {
	Surface(
		shape = MaterialTheme.shapes.medium,
		color = color,
		contentColor = MaterialTheme.colorScheme.onSurface,
		tonalElevation = 2.dp,
		modifier = modifier
	) {
		Column(
			modifier = Modifier
				.padding(Spacing.screenEdge)
				.width(IntrinsicSize.Max)
				.height(IntrinsicSize.Max)
		) {
			Text(text = name, style = MaterialTheme.typography.titleLarge)
			Spacer(Modifier.height(Spacing.sectionContent))
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(
					text = "Matches",
					overflow = TextOverflow.Ellipsis,
					maxLines = 1,
					modifier = Modifier
						.weight(1f, fill = false)
						.padding(end = 4.dp)
				)
				Text(text = noOfMatches)
			}
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(
					text = "High score",
					overflow = TextOverflow.Ellipsis,
					maxLines = 1,
					modifier = Modifier
						.weight(1f, fill = false)
						.padding(end = 16.dp)
				)
				Text(text = highScore)
			}
		}
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
	MedianMeepleTheme {
		GameCard(
			name = "Wingspan",
			color = GameDomainModel.DisplayColors[3],
			highScore = "100",
			noOfMatches = "5",
		)
	}
}
