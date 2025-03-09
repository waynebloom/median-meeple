package com.waynebloom.scorekeeper.auth.data.source

import com.waynebloom.scorekeeper.auth.data.model.JWT
import com.waynebloom.scorekeeper.auth.domain.model.LoginBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
	@POST("login")
	suspend fun login(@Body body: LoginBody): Response<JWT>
}