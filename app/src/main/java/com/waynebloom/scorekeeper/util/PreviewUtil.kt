package com.waynebloom.scorekeeper.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreviewContainer(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {

	Surface(
		color = MaterialTheme.colorScheme.background,
		modifier = modifier
	) {
		Box(Modifier.padding(16.dp)) {
			content()
		}
	}
}