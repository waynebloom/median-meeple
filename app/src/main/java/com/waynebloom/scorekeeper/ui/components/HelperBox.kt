package com.waynebloom.scorekeeper.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun HelperBox(
	message: String,
	type: HelperBoxType,
	modifier: Modifier = Modifier,
	maxLines: Int = 10,
) {
	val icon = when (type) {
		HelperBoxType.Info -> painterResource(id = R.drawable.ic_info_circle)
		HelperBoxType.Error -> painterResource(id = R.drawable.ic_error_circle)
		HelperBoxType.Missing -> painterResource(id = R.drawable.ic_help_circle)
	}
	val cardColors = when (type) {
		HelperBoxType.Error -> CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.errorContainer,
			contentColor = MaterialTheme.colorScheme.onErrorContainer
		)

		HelperBoxType.Missing -> CardDefaults.cardColors()
		else -> CardDefaults.cardColors()
	}

	Card(
		colors = cardColors,
		modifier = modifier.fillMaxWidth()
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
			modifier = Modifier.padding(Spacing.sectionContent),
		) {

			Icon(
				painter = icon,
				contentDescription = null,
				modifier = Modifier.align(Alignment.Top)
			)

			Text(
				text = message,
				maxLines = maxLines,
				overflow = TextOverflow.Ellipsis,
				modifier = Modifier.align(Alignment.CenterVertically)
			)
		}
	}
	/*Box(
			contentAlignment = Alignment.CenterStart,
			modifier = modifier
					.border(
							width = 1.dp,
							color = borderColor,
							shape = MaterialTheme.shapes.medium
					)
					.clip(MaterialTheme.shapes.medium)
					.background(color = backgroundColor)
					.fillMaxWidth()
	) {

			Row(
					horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
					modifier = Modifier.padding(Spacing.sectionContent),
			) {

					Icon(
							painter = icon,
							contentDescription = null,
							tint = foregroundColor,
							modifier = Modifier.align(Alignment.Top))

					Text(
							text = message,
							color = foregroundColor,
							maxLines = maxLines,
							overflow = TextOverflow.Ellipsis,
							modifier = Modifier.align(Alignment.CenterVertically))
			}
	}*/
}

enum class HelperBoxType {
	Info,
	Error,
	Missing;
}

@Preview(name = "Short, Light")
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Short, Dark")
@Composable
fun HelperBoxShortPreview() {
	MedianMeepleTheme {

		Surface(color = MaterialTheme.colorScheme.background) {
			HelperBox(message = "This is a test message.", type = HelperBoxType.Info)
		}
	}
}

@Preview(name = "Long, Light")
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Long, Dark")
@Composable
fun HelperBoxLongPreview() {
	MedianMeepleTheme {

		Surface(color = MaterialTheme.colorScheme.background) {
			HelperBox(
				message = "This is a long test message. It should span more than one line.",
				type = HelperBoxType.Info
			)
		}
	}
}

@Preview(name = "Missing, Light")
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Missing, Dark")
@Composable
fun HelperBoxMissingPreview() {
	MedianMeepleTheme {

		Surface(color = MaterialTheme.colorScheme.background) {
			HelperBox(message = "This is a test message.", type = HelperBoxType.Missing)
		}
	}
}

@Preview(name = "Error, Light")
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Error, Dark")
@Composable
fun HelperBoxErrorPreview() {
	MedianMeepleTheme {

		Surface(color = MaterialTheme.colorScheme.background) {
			HelperBox(message = "This is a test message.", type = HelperBoxType.Error)
		}
	}
}
