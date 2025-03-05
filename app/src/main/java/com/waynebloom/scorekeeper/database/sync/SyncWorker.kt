package com.waynebloom.scorekeeper.database.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.Success
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import com.waynebloom.scorekeeper.database.supabase.data.model.Change
import com.waynebloom.scorekeeper.util.PreferencesManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.Instant


@HiltWorker
class SyncWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted workerParams: WorkerParameters,
	private val supabaseApi: SupabaseApi,
	private val preferencesManager: PreferencesManager,
) : CoroutineWorker(appContext, workerParams) {

	companion object {
		const val SYNC_WORK_NAME = "SyncWithRemote"

		fun expeditedSync(): OneTimeWorkRequest {
			val constraints = Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build()
			return OneTimeWorkRequestBuilder<SyncWorker>()
				.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
				.setConstraints(constraints)
				.build()
		}
	}

	override suspend fun doWork(): Result {
		/**
		 * TODO:
		 *
		 * 	maintain a lastUpdated field on local storage
		 * 	<every so often>, query the remote changelog for records occurring after lastUpdated
		 *
		 * 	if empty:
		 * 		Result.success
		 *
		 * 	if !empty:
		 * 		changes.forEach:
		 *			delegate updates to the repository that corresponds to change.table_name
		 *
		 *	type change:
		 *		id,
		 *		timestamp,
		 *		action,
		 *		table_name,
		 *		old_data,
		 *		new_data,
		 */

		withContext(Dispatchers.IO) {
			val lastSynced = preferencesManager.getLastSynced()

			if (lastSynced == null) {
				preferencesManager.setLastSynced(Instant.now().toString())
				// TODO: decide what to do here, probably do an initial DB fetch
				return@withContext Result.success()
			}
			val lastSyncedStub = Instant.now().minusSeconds(3600).toString()
			println("WBDEBUG: Getting changes occurring after $lastSyncedStub")
			val changes = supabaseApi.getChangesAfter(lastSyncedStub)
			println("WBDEBUG: Changes are $changes")
		}

		return Result.success()
	}
}