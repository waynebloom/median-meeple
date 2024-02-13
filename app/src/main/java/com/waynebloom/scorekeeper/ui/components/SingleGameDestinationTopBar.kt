package com.waynebloom.scorekeeper.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun SingleGameDestinationTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showEditAction: Boolean = true,
    onEditClick: () -> Unit = {},
    showCloseAction: Boolean = true,
    onCloseClick: () -> Unit = {}
) {

    Surface(color = MaterialTheme.colors.primary) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {

            if (showCloseAction) {
                IconButton(
                    painter = painterResource(R.drawable.ic_x),
                    modifier = Modifier.shadow(elevation = 2.dp, shape = CircleShape),
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    shape = CircleShape,
                    visibleSize = 40.dp,
                    onClick = onCloseClick
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (showEditAction) {
                IconButton(
                    painter = painterResource(R.drawable.ic_edit),
                    modifier = Modifier.shadow(elevation = 2.dp, shape = CircleShape),
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    shape = CircleShape,
                    visibleSize = 40.dp,
                    onClick = onEditClick
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SingleGameDestinationTopBarPreview() {
    MedianMeepleTheme {
        SingleGameDestinationTopBar(title = "Wingspan")
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SingleGameDestinationTopBarWithAScreenPreview() {
    MedianMeepleTheme {
        Scaffold(
            topBar = {
                SingleGameDestinationTopBar(title = "Wingspan")
            }
        ) {

            Box(modifier = Modifier.padding(it)) {

            }
        }
    }
}