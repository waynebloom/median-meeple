package com.waynebloom.scorekeeper.auth.domain.model

import com.squareup.moshi.Json

data class LoginBody(
    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val pw: String,
)