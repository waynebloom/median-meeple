package com.waynebloom.scorekeeper.admob.domain.usecase

import com.waynebloom.scorekeeper.admob.data.repository.AdRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAdAsFlow @Inject constructor(
    private val adRepository: AdRepository
) {

    operator fun invoke() = adRepository.adFlow.map {
        it.first()
    }
}
