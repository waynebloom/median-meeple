package com.waynebloom.scorekeeper.admob.domain.usecase

import com.waynebloom.scorekeeper.admob.data.repository.AdRepository
import javax.inject.Inject

class GetMultipleAdsAsFlow @Inject constructor(
    private val adRepository: AdRepository
) {

    operator fun invoke() = adRepository.adFlow
}
