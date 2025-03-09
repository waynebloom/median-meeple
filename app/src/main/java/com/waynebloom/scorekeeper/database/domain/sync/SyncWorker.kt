package com.waynebloom.scorekeeper.database.domain.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.squareup.moshi.Moshi
import com.waynebloom.scorekeeper.database.domain.CategoryRepository
import com.waynebloom.scorekeeper.database.domain.GameRepository
import com.waynebloom.scorekeeper.database.domain.MatchRepository
import com.waynebloom.scorekeeper.database.domain.PlayerRepository
import com.waynebloom.scorekeeper.database.domain.ScoreRepository
import com.waynebloom.scorekeeper.database.domain.model.Action
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import com.waynebloom.scorekeeper.util.PreferencesManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant


@HiltWorker
class SyncWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted workerParams: WorkerParameters,
	private val supabaseApi: SupabaseApi,
	private val preferencesManager: PreferencesManager,
	private val gameRepository: GameRepository,
	private val matchRepository: MatchRepository,
	private val playerRepository: PlayerRepository,
	private val categoryRepository: CategoryRepository,
	private val scoreRepository: ScoreRepository,
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

		return withContext(Dispatchers.IO) {
			val lastSynced = preferencesManager.getLastSynced()

			/**
			 * TODO
			 *
			 * When lastSynced is null, it needs to trigger an initial sync of the whole DB. It would make
			 * sense to consider batching this request, as a user could theoretically have a huge amount
			 * of data stored.
			 */
			if (lastSynced == null) {
				preferencesManager.setLastSynced(Instant.now().toString())

				/* TODO: perform an initial database sync */

				return@withContext Result.success()
			}

			Log.d(SyncWorker::class.simpleName, "Getting changes occurring after $lastSynced.")
			val changes = supabaseApi.getChangesAfter(lastSynced)
			Log.d(SyncWorker::class.simpleName, "Changes are $changes.")

			/* TODO: propagate the fetched changes to the local database copy */

			withContext(Dispatchers.Default) {

				changes.forEach {
					val action = Action.fromString(it.action)
					val jsonEntity = when (action) {
						Action.DELETE -> {
							it.oldData
								?: throw Exception("No entity provided for DELETE sync operation.")
						}

						Action.UPDATE, Action.INSERT -> {
							it.newData
								?: throw Exception("No entity provided for ${action.name} sync operation.")
						}
					}

					val syncHandler: SyncHandler = when (it.tableName) {
						"games" -> gameRepository
						"matches" -> matchRepository
						"players" -> playerRepository
						"categories" -> categoryRepository
						"scores" -> scoreRepository
						else -> {
							throw Exception("Encountered an invalid table name: \"${it.tableName}\".")
						}
					}

					syncHandler.sync(change = Pair(action, jsonEntity))
				}
			}

			/* TODO: implement some system to avoid "syncing" with changes that were pushed
			   	by the local device
			*/

			// TODO: create some custom exceptions to represent the various errors that can occur
			// 	in this flow

			preferencesManager.setLastSynced(Instant.now().toString())

			Result.success()
		}
	}
}