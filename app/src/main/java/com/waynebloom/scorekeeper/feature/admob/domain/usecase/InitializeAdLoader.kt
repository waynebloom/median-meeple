package com.waynebloom.scorekeeper.feature.admob.domain.usecase

import com.waynebloom.scorekeeper.feature.admob.data.repository.AdRepository
import javax.inject.Inject

class InitializeAdLoader @Inject constructor(
	private val adRepository: AdRepository
) {
	operator fun invoke() {
		adRepository.setUpAdLoader()
	}
}