package com.waynebloom.scorekeeper.database.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val SYNC_WORK_NAME = "SyncWithRemote"

@HiltWorker
class SyncWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {

	companion object {
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

	override fun doWork(): Result {
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

		return Result.success()
	}
}