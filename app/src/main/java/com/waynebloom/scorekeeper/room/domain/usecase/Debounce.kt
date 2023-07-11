package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.constants.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Debounce @Inject constructor(
    private val coroutineScope: CoroutineScope
) {

    private var job: Job? = null
    private val duration = Constants
        .debounceDurationMs
        .toDuration(DurationUnit.MILLISECONDS)

    operator fun invoke(immediately: Boolean = false, function: suspend () -> Unit) {
        job?.cancel()
        job = coroutineScope.launch {
            if (!immediately) delay(duration)
            function()
        }
    }
}