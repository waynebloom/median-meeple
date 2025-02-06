package com.waynebloom.scorekeeper.network.data.datasource

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.GET

interface MeepleBaseApi {
    @GET("games")
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