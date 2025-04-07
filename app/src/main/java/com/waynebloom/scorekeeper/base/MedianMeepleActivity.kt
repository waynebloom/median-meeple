package com.waynebloom.scorekeeper.base

import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.admob.domain.usecase.InitializeAdLoader
import com.waynebloom.scorekeeper.navigation.MedianMeepleNavHost
import com.waynebloom.scorekeeper.navigation.graph.sendFeedbackEmail
import com.waynebloom.scorekeeper.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MedianMeepleActivity : ComponentActivity() {

	@Inject
	lateinit var initializeAdLoader: InitializeAdLoader

	@Inject
	lateinit var coroutineScope: CoroutineScope

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		initializeAdLoader()
		coroutineScope.launch {
			MobileAds.initialize(this@MedianMeepleActivity)
		}

		enableEdgeToEdge()

		setContent {
			val viewModel: MainViewModel = hiltViewModel()
			val appearanceMode by viewModel.appearanceMode.collectAsState()
			val isInDarkTheme = when(appearanceMode) {
				AppearanceMode.SYSTEM -> isSystemInDarkTheme()
				AppearanceMode.DARK -> true
				AppearanceMode.LIGHT -> false
			}
			MedianMeepleTheme(isInDarkTheme = isInDarkTheme) {
				MedianMeepleNavHost(
					onSendFeedback = ::sendFeedbackEmail
				)
			}
		}
	}
}
