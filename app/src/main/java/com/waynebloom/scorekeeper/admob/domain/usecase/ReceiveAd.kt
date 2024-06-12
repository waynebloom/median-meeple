package com.waynebloom.scorekeeper.admob.domain.usecase

import com.waynebloom.scorekeeper.admob.data.repository.AdRepository
import javax.inject.Inject

class ReceiveAd @Inject constructor(
    private val adRepository: AdRepository
) {

    suspend operator fun invoke() = adRepository.receiveAd()
}
