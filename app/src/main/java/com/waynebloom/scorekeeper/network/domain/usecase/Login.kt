package com.waynebloom.scorekeeper.network.domain.usecase

import com.waynebloom.scorekeeper.network.data.datasource.MeepleBaseApi
import javax.inject.Inject

class Login @Inject constructor(
    private val meepleBaseApi: MeepleBaseApi
) {
    // operator fun invoke() = flow {
    //     meepleBaseApi.login()
    // }
}