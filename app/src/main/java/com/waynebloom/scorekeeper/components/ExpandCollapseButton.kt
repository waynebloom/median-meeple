package com.waynebloom.scorekeeper.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandCollapseButton(
	text: String?,
	expanded: Boolean,
	onClick: () -> Unit,
) {

	Surface(
		color = MaterialTheme.colorScheme.secondaryContainer,
		shape = CircleShape,
		modifier = Modifier
			.minimumInteractiveComponentSize()
			.clip(CircleShape)
			.clickable(onClick = onClick)
	) {

		Box(contentAlignment = Alignment.Center) {
			AnimatedContent(
				targetState = expanded,
				transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
				label = "",
			) { buttonIsExpanded ->
				if (buttonIsExpanded) {
					Icon(
						painter = painterResource(id = R.drawable.ic_chevron_up),
						contentDescription = null,
						modifier = Modifier
							.padding(Dimensions.Spacing.sectionContent)
							.size(20.dp)
					)
				} else {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.padding(
							horizontal = Dimensions.Spacing.sectionContent,
							vertical = 8.dp
						)
					) {
						if (text != null) {
							Text(
								text = text,
								modifier = Modifier.padding(end = 4.dp)
							)
						}

						Icon(
							painter = painterResource(id = R.drawable.ic_chevron_down),
							contentDescription = null,
							modifier = Modifier.size(20.dp)
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun ExpandCollapseButtonPreview() {
	MedianMeepleTheme {
		ExpandCollapseButton(
			text = "Expand",
			expanded = false,
			onClick = {}
		)
	}
}
