package com.waynebloom.scorekeeper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.waynebloom.scorekeeper.ui.constants.Dimensions

@Composable
fun BasicTopBar(
	title: String,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.windowInsetsPadding(WindowInsets.statusBars)
			.heightIn(min = Dimensions.Size.topBarHeight)
			.fillMaxWidth()
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
		)
	}
}
