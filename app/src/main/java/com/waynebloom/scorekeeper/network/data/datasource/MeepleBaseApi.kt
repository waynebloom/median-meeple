package com.waynebloom.scorekeeper.network.data.datasource

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface MeepleBaseApi {
    @POST("login")
    suspend fun login(): Response<String>

    @GET("games")
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFkbWluQHRlc3QuY29tIiwiZXhwIjoxNzM3Nzc1MTg5LCJ1c2VySUQiOjF9.hySmqtzHuKKZHwSxcLlFYg7K7opIObHY3Lf5cvOHbXE")
    suspend fun getGames(): Response<List<GameModelRemote>>
}

data class GameModelRemote(
    @Json(name = "id")
    val id: Int,

    @Json(name = "color")
    val color: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "scoring_mode")
    val scoringMode: Int,
)