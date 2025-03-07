package com.waynebloom.scorekeeper.database.supabase.data.datasource

import com.waynebloom.scorekeeper.database.supabase.data.model.ChangeNetworkModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupabaseApi @Inject constructor(
	private val postgrest: Postgrest,
) {

	suspend fun getChangesAfter(timestamp: String): List<ChangeNetworkModel> {
		return withContext(Dispatchers.IO) {
			postgrest
				.from("changelog")
				.select {
					filter {
						ChangeNetworkModel::timestamp gt timestamp
					}
				}
				.decodeList<ChangeNetworkModel>()
		}
	}
}