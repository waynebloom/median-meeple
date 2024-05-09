package com.waynebloom.scorekeeper.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.navigation.MedianMeepleApp
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.admob.AdService
import com.waynebloom.scorekeeper.admob.domain.usecase.InitializeAdFlowAndLoader
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MedianMeepleActivity : ComponentActivity() {

    @Inject
    lateinit var initializeAdFlowAndLoader: InitializeAdFlowAndLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeAdFlowAndLoader()
        MobileAds.initialize(this)

        setContent {
            MedianMeepleTheme {
                MedianMeepleApp()
            }
        }
    }
}
