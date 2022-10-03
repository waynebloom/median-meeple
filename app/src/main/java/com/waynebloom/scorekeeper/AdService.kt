package com.waynebloom.scorekeeper

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd

class AdService(context: Context) {
    var adLoader: AdLoader? = null
    var currentAd: NativeAd? = null

    init {
        adLoader = AdLoader.Builder(
            context,
            if (BuildConfig.DEBUG) AdmobID.DEBUG.id else AdmobID.RELEASE.id
        )
            .forNativeAd {
                currentAd = it
                LocalNativeAd = compositionLocalOf { currentAd }
            }
            .build()
    }

    fun destroyAd() {
        currentAd?.destroy()
    }

    fun loadNewAd() {
        adLoader?.loadAd(AdRequest.Builder().build())
    }
}