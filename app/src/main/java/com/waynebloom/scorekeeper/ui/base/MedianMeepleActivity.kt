package com.waynebloom.scorekeeper.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.ui.navigation.NavHost
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.admob.AdService
import com.waynebloom.scorekeeper.admob.domain.usecase.InitializeAdFlowAndLoader
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MedianMeepleActivity : ComponentActivity() {

    private lateinit var viewModel: MedianMeepleActivityViewModel

    @Inject
    lateinit var initializeAdFlowAndLoader: InitializeAdFlowAndLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MedianMeepleActivityViewModel::class.java]

        // TODO: Both of these are here for now until the rework is complete
        initializeAdFlowAndLoader()
        MobileAds.initialize(this)

        setContent {
            App(viewModel = viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.adService.destroyAd()
    }
}

@Composable
private fun App(viewModel: MedianMeepleActivityViewModel) {
    MedianMeepleTheme {

        LaunchedEffect(true) {
            while (true) {
                viewModel.adService.loadAd()
                delay(AdService.NewAdRequestDelayMs)
                viewModel.adService.currentAd.value = null
                delay(AdService.BetweenAdsDelayMs)
            }
        }

        Scaffold { innerPadding ->

            NavHost(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
