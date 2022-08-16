package com.waynebloom.highscores.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ScreenHeader(
    title: String,
    color: Color,
    headerButton: @Composable () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.background(color.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .height(120.dp)
                .weight(1f, fill = false)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                color = color,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(0.85f, fill = false)
            )
            headerButton()
        }
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colors.background,
                    MaterialTheme.shapes.medium.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                )
                .fillMaxWidth()
                .height(16.dp)
        )
    }
}