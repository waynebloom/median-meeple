package com.waynebloom.scorekeeper.admob.data.datasource

import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.BuildConfig
import com.waynebloom.scorekeeper.dagger.wrapper.GoogleAdsWrapper
import com.waynebloom.scorekeeper.admob.AdmobID
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdRemoteDataSource @Inject constructor(
    private val googleAdsWrapper: GoogleAdsWrapper,
) {

    var adFlow: MutableStateFlow<NativeAd?>
        private set
    private val loader: AdLoader

    init {
        val adUnitId = if (BuildConfig.DEBUG) AdmobID.DEBUG.id else AdmobID.RELEASE.id

        adFlow = MutableStateFlow(null)
        loader = googleAdsWrapper
            .getAdLoaderBuilder(adUnitId)
            .forNativeAd { adFlow.tryEmit(it) }
            .build()
    }

    fun destroyAd() {
        adFlow.value?.destroy()
        adFlow.tryEmit(null)
    }

    fun loadAd() {
        val request = googleAdsWrapper.getAdRequestBuilder().build()
        loader.loadAd(request)
    }
}
