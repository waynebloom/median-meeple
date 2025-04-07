package com.waynebloom.scorekeeper.auth.domain.model

import java.time.Instant
import com.waynebloom.scorekeeper.auth.data.model.JWT as JWTDataModel

data class JWT(
	val token: String,
	val expiresOn: Instant,
) {

	companion object {
		fun fromDataModel(jwt: JWTDataModel) = JWT(
			token = jwt.token,
			expiresOn = Instant.parse(jwt.exp)
		)
	}
}