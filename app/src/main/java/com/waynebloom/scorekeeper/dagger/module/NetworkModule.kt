package com.waynebloom.scorekeeper.dagger.module

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waynebloom.scorekeeper.auth.CredentialManager
import com.waynebloom.scorekeeper.auth.data.source.AuthApi
import com.waynebloom.scorekeeper.network.data.datasource.MeepleBaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
    fun providesMeepleBaseApi(moshi: Moshi, cm: CredentialManager): MeepleBaseApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val transformed = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${cm.get().token}")
                    .build()
                chain.proceed(transformed)
            }.build()

        val retrofitAuth = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofitAuth.create(MeepleBaseApi::class.java)
    }

    @Provides
    fun providesAuthApi(moshi: Moshi): AuthApi = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(AuthApi::class.java)
}
