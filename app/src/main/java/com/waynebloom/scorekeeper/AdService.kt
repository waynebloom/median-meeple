package com.waynebloom.scorekeeper

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.enums.AdmobID

class AdService(context: Context) {
    private var adLoader: AdLoader? = null
    var currentAd: MutableState<NativeAd?> = mutableStateOf(null)

    companion object {
        const val NEW_AD_REQUEST_DELAY_MS = 60000L
        const val BETWEEN_ADS_DELAY_MS = 500L
    }

    init {
        adLoader = AdLoader.Builder(
            context,
            if (BuildConfig.DEBUG) AdmobID.DEBUG.id else AdmobID.RELEASE.id
        )
            .forNativeAd {
                currentAd.value = it
            }
            .build()
    }

    fun destroyAd() {
        currentAd.value?.destroy()
    }

    fun loadNewAd() {
        adLoader?.loadAd(
            AdRequest.Builder()
                .build()
        )
    }
}