package com.waynebloom.scorekeeper.shared.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetString @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(id: Int): String = context.getString(id)
}