package com.waynebloom.scorekeeper.base

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.waynebloom.scorekeeper.database.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MedianMeepleApplication : Application(), Configuration.Provider {

	// TODO: research this at more length. I want to understand it better, as it might not be
	// 	necessary
	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.build()

	override fun onCreate() {
		super.onCreate()

		WorkManager.getInstance(this).apply {
			enqueueUniqueWork(
				SyncWorker.SYNC_WORK_NAME,
				ExistingWorkPolicy.KEEP,
				SyncWorker.expeditedSync()
			)
		}
	}
}
