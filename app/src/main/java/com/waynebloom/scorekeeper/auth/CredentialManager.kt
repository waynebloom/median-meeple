package com.waynebloom.scorekeeper.auth

import com.waynebloom.scorekeeper.auth.domain.model.JWT
import java.time.Instant
import javax.inject.Inject

class CredentialManager @Inject constructor() {

	// FIXME: reimplement auth w/ Supabase

	private companion object {
		var jwt: JWT? = null
	}

	fun clear() {
		jwt = null
	}

	fun get() = jwt

	fun isEmpty() = jwt == null

	fun requireLogin(): Boolean {
		return Instant.now().isBefore(jwt?.expiresOn)
	}

	fun store(newJWT: JWT) {
		jwt = newJWT
	}
}
