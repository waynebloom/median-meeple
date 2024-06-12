package com.waynebloom.scorekeeper.admob.data.repository

import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.data.datasource.AdRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Singleton
class AdRepository @Inject constructor(
    private val adRemoteDataSource: AdRemoteDataSource,
    private val coroutineScope: CoroutineScope
) {

    private lateinit var adLoaderJob: Job
    private val loadedAds = mutableListOf<NativeAd>()
    val adFlow: MutableStateFlow<List<NativeAd>> = MutableStateFlow(emptyList())
    private val adChannel: Channel<NativeAd> = Channel(capacity = 5)

    companion object {
        const val NewAdRequestDelaySeconds = 25L
        // TODO: remove?
        const val BetweenAdsDelayMs = 500L
    }

    // TODO: maybe remove this if the policy question is answered

    suspend fun receiveAd(): NativeAd {
        return adChannel.receive().also {
            adRemoteDataSource.loadAd()
        }
    }

    // TODO: keep this?

    fun setUpAdLoader() {
        adRemoteDataSource.initializeLoader { nativeAd ->
            if (loadedAds.size == 5) {
                purgeAds(emitEmptyState = true)
            }
            loadedAds += nativeAd
            if (loadedAds.size == 5) {
                adFlow.tryEmit(loadedAds.toList())
            }
        }
        adLoaderJob = launchAdLoader()
        adLoaderJob.invokeOnCompletion {
            purgeAds()
        }
    }

    private fun purgeAds(emitEmptyState: Boolean = false) {
        if (emitEmptyState) {
            adFlow.tryEmit(emptyList())
        }
        loadedAds.forEach { it.destroy() }
        loadedAds.clear()
    }

    private fun launchAdLoader() = coroutineScope.launch {
        while (true) {
            adRemoteDataSource.loadAds(5)
            delay(NewAdRequestDelaySeconds.toDuration(DurationUnit.SECONDS))
            // TODO: remove?
//            delay(BetweenAdsDelayMs.toDuration(DurationUnit.MILLISECONDS))
        }
    }
}
