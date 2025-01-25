package com.waynebloom.scorekeeper.dagger.module

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waynebloom.scorekeeper.network.data.datasource.MeepleBaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // NOTE: This seems to be the IP of my computer from the POV of the emulator
    //       this means that it will not work the same on my phone.
    private const val baseURL = "http://172.20.146.180:8080/"

    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun providesRetrofit(moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    fun providesMeepleBaseApi(retrofit: Retrofit): MeepleBaseApi = retrofit
        .create(MeepleBaseApi::class.java)
}
