package com.waynebloom.scorekeeper.auth

import com.waynebloom.scorekeeper.auth.domain.model.JWT
import java.time.Instant
import javax.inject.Inject

class CredentialManager @Inject constructor() {
	private companion object {
		var jwt = JWT.empty()
	}

	fun clear() {
		jwt = JWT.empty()
	}

	fun get() = jwt

	fun isEmpty() = jwt == JWT.empty()

	fun requireLogin(): Boolean {
		return Instant.now().isBefore(jwt.expiresOn)
	}

	fun store(newJWT: JWT) {
		jwt = newJWT
	}
}
