package com.waynebloom.scorekeeper.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.admob.domain.usecase.InitializeAdLoader
import com.waynebloom.scorekeeper.navigation.MedianMeepleApp
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
			MedianMeepleTheme {
				MedianMeepleApp()
			}
		}
	}
}
