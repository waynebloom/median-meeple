package com.waynebloom.scorekeeper.admob.data.repository

import com.waynebloom.scorekeeper.admob.data.datasource.AdRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AdRepository @Inject constructor(
    private val adRemoteDataSource: AdRemoteDataSource,
    private val coroutineScope: CoroutineScope
) {

    private lateinit var adLoaderJob: Job

    companion object {
        const val NewAdRequestDelaySeconds = 30L
        const val BetweenAdsDelayMs = 500L
    }

    fun initializeAdLoaderAndFlow() {
        adLoaderJob = launchAdLoader().apply {
            invokeOnCompletion {
                adRemoteDataSource.destroyAd()
            }
        }
    }

    fun observeAd() = adRemoteDataSource.adFlow

    private fun launchAdLoader() = coroutineScope.launch {
        while (true) {
            adRemoteDataSource.loadAd()
            delay(NewAdRequestDelaySeconds.toDuration(DurationUnit.SECONDS))
            adRemoteDataSource.destroyAd()
            delay(BetweenAdsDelayMs.toDuration(DurationUnit.MILLISECONDS))
        }
    }
}