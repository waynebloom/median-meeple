package com.waynebloom.scorekeeper.auth.domain.usecase

import com.waynebloom.scorekeeper.auth.CredentialManager
import com.waynebloom.scorekeeper.auth.data.source.AuthApi
import com.waynebloom.scorekeeper.auth.domain.model.LoginBody
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.waynebloom.scorekeeper.auth.domain.model.JWT as DomainJWT

class Login @Inject constructor(
    private val authApi: AuthApi,
    private val cm: CredentialManager,
) {
     operator fun invoke(email: String, pw: String) = flow {
         val body = LoginBody(email, pw)
         val dataJWT = authApi
             .login(body)
             .body()

         if (dataJWT == null) {
             emit(false)
             return@flow
         }

         cm.store(DomainJWT.fromDataModel(dataJWT))
         emit(true)
     }
}