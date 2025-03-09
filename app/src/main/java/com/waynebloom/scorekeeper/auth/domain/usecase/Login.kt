package com.waynebloom.scorekeeper.auth.domain.usecase

/*
FIXME: reimplement/remove
class Login @Inject constructor(
	private val authApi: AuthApi,
	private val cm: CredentialManager,
	private val pm: PreferencesManager,
) {
	operator fun invoke(email: String, pw: String) = flow {
		val requestBody = LoginBody(email, pw)
		authApi.login(requestBody).let { response ->
			if (response.code() != 200) {
				emit(false)
				return@flow
			}

			response.body().let { body ->
				if (body == null) {
					emit(false)
					return@flow
				}

				pm.setEmail(email)
				cm.store(DomainJWT.fromDataModel(body))
				emit(true)
			}
		}
	}
}*/
