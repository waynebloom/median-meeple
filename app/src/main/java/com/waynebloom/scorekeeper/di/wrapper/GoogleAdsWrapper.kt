package com.waynebloom.scorekeeper.di.wrapper

import android.content.Context
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleAdsWrapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getAdLoaderBuilder(adUnitId: String) = AdLoader.Builder(context, adUnitId)

    fun getAdRequestBuilder() = AdRequest.Builder()
}