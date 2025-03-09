package com.waynebloom.scorekeeper.dagger.module

import android.content.Context
import androidx.room.Room
import com.waynebloom.scorekeeper.database.room.data.datasource.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Singleton
	@Provides
	fun providesAppDatabase(@ApplicationContext context: Context) =
		Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"app_database"
		)
			.createFromAsset("database/scores_app.db")
			.addMigrations(*AppDatabase.manualMigrations)
			.build()

	@Provides
	fun providesCategoryDao(appDatabase: AppDatabase) = appDatabase.getCategoryDao()

	@Provides
	fun providesCategoryScoreDao(appDatabase: AppDatabase) = appDatabase.getCategoryScoreDao()

	@Provides
	fun providesGameDao(appDatabase: AppDatabase) = appDatabase.getGameDao()

	@Provides
	fun providesMatchDao(appDatabase: AppDatabase) = appDatabase.getMatchDao()

	@Provides
	fun providesPlayerDao(appDatabase: AppDatabase) = appDatabase.getPlayerDao()
}








