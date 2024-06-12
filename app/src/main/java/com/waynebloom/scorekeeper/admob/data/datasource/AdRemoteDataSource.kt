package com.waynebloom.scorekeeper.admob.data.datasource

import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.waynebloom.scorekeeper.BuildConfig
import com.waynebloom.scorekeeper.admob.AdmobID
import com.waynebloom.scorekeeper.dagger.wrapper.GoogleAdsWrapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdRemoteDataSource @Inject constructor(
    private val googleAdsWrapper: GoogleAdsWrapper,
) {

    private lateinit var loader: AdLoader

    internal fun initializeLoader(onNativeAdLoaded: (NativeAd) -> Unit) {
        val adUnitId = if (BuildConfig.DEBUG) AdmobID.DEBUG.id else AdmobID.RELEASE.id
        val options = googleAdsWrapper.getNativeAdOptionsBuilder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_LEFT)
            .build()

        loader = googleAdsWrapper
            .getAdLoaderBuilder(adUnitId)
            .withNativeAdOptions(options)
            .forNativeAd { onNativeAdLoaded(it) }
            .build()
    }

    fun loadAds(count: Int) {
        repeat(count) {
            val request = googleAdsWrapper.getAdRequestBuilder().build()
            loader.loadAd(request)
        }
    }
}
