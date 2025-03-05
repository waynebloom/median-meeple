package com.waynebloom.scorekeeper.database.supabase.data.datasource

import com.waynebloom.scorekeeper.database.supabase.data.model.Change
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupabaseApi @Inject constructor(
	private val postgrest: Postgrest,
) {

	suspend fun getChangesAfter(timestamp: String): List<Change> {
		return withContext(Dispatchers.IO) {
			postgrest
				.from("changelog")
				.select {
					filter {
						Change::timestamp gt timestamp
					}
				}
				.decodeList<Change>()
		}
	}
}