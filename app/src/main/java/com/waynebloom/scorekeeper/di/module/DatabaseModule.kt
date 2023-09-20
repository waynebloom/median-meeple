package com.waynebloom.scorekeeper.di.module

import android.content.Context
import androidx.room.Room
import com.waynebloom.scorekeeper.room.data.datasource.AppDatabase
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
    fun providesCategoryRepository(appDatabase: AppDatabase) = appDatabase.getCategoryRepository()

    @Provides
    fun providesGameRepository(appDatabase: AppDatabase) = appDatabase.getGameRepository()

    @Provides
    fun providesMatchRepository(appDatabase: AppDatabase) = appDatabase.getMatchRepository()
}








