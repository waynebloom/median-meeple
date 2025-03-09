package com.waynebloom.scorekeeper.dagger.factory

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MutableStateFlowFactory @Inject constructor() {

	fun <T> newInstance(initialValue: T) = MutableStateFlow(initialValue)
}
